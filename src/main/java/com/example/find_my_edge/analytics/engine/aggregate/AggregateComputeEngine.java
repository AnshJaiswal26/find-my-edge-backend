package com.example.find_my_edge.analytics.engine.aggregate;

import com.example.find_my_edge.analytics.ast.context.SchemaType;
import com.example.find_my_edge.analytics.ast.exception.AstException;
import com.example.find_my_edge.analytics.ast.exception.AstExecutionException;
import com.example.find_my_edge.analytics.ast.executor.AggregateExecutor;
import com.example.find_my_edge.analytics.ast.function.enums.FunctionMode;
import com.example.find_my_edge.analytics.ast.mapper.AstNodeMapper;
import com.example.find_my_edge.analytics.ast.model.AstNode;
import com.example.find_my_edge.analytics.ast.model.AstResult;
import com.example.find_my_edge.analytics.ast.parser.AstPipeline;
import com.example.find_my_edge.analytics.ast.util.SchemaTypeResolver;
import com.example.find_my_edge.analytics.engine.dataSet.TradeDataset;
import com.example.find_my_edge.common.config.uiconfigs.AstConfig;
import com.example.find_my_edge.schema.model.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
public class AggregateComputeEngine {

    private final AggregateExecutor aggregateExecutor;
    private final AstPipeline astPipeline;

    private final ExecutorService computeExecutor;


    public Map<String, Double> computeAggregate(
            Map<String, String> formulas,
            Map<String, AstConfig> astConfigs,
            Map<String, Schema> schemasById,
            TradeDataset tradeDataset
    ) {

        Map<String, AstNode> astMap = buildAstMap(formulas, astConfigs, schemasById);

        Map<String, SchemaType> schemaTypeMap =
                SchemaTypeResolver.buildSchemaTypeMap(schemasById);

        Map<String, Future<Double>> futures = new HashMap<>();

        for (Map.Entry<String, AstNode> entry : astMap.entrySet()) {

            String key = entry.getKey();
            AstNode astNode = entry.getValue();

            futures.put(
                    key, computeExecutor.submit(() -> {

                        Object result = aggregateExecutor.execute(
                                astNode,
                                tradeDataset::getValue,
                                tradeDataset::size,
                                schemaTypeMap::get
                        );

                        if (result == null) return null;

                        if (!(result instanceof Number)) {
                            throw new AstException("Non-numeric result for key: " + key);
                        }

                        return ((Number) result).doubleValue();
                    })
            );
        }

        return collectFutures(futures);
    }

    // ============================
    // HELPERS
    // ============================

    private Map<String, AstNode> buildAstMap(
            Map<String, String> formulas,
            Map<String, AstConfig> astConfigs,
            Map<String, Schema> schemasById
    ) {

        Map<String, AstNode> astMap = new HashMap<>();

        if (formulas != null) {
            for (Map.Entry<String, String> entry : formulas.entrySet()) {
                AstResult result = astPipeline.buildAst(
                        entry.getValue(),
                        FunctionMode.AGGREGATE,
                        schemasById
                );
                astMap.put(entry.getKey(), result.getAstNode());
            }
        }

        if (astConfigs != null) {
            for (Map.Entry<String, AstConfig> entry : astConfigs.entrySet()) {
                astMap.put(entry.getKey(), AstNodeMapper.toNode(entry.getValue()));
            }
        }

        return astMap;
    }

    private Map<String, Double> collectFutures(Map<String, Future<Double>> futures) {
        Map<String, Double> result = new HashMap<>();

        for (Map.Entry<String, Future<Double>> entry : futures.entrySet()) {
            try {
                result.put(entry.getKey(), entry.getValue().get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new AstException("Thread interrupted", e);
            } catch (ExecutionException e) {
                throw new AstException(
                        "Error computing key: " + entry.getKey(),
                        e.getCause()
                );
            }
        }

        return result;
    }

    public Double computedAggregate(
            AstConfig astConfig,
            String formula,
            Map<String, Schema> schemasById,
            TradeDataset tradeDataset
    ) {

        AstNode astNode;

        if (formula != null) {
            astNode = astPipeline.buildAst(
                    formula,
                    FunctionMode.AGGREGATE,
                    schemasById
            ).getAstNode();
        } else {
            astNode = AstNodeMapper.toNode(astConfig);
        }

        if (astNode == null) throw new AstExecutionException("Ast not found");

        Map<String, SchemaType> schemaTypeMap =
                SchemaTypeResolver.buildSchemaTypeMap(schemasById);

        Object result = aggregateExecutor.execute(
                astNode,
                tradeDataset::getValue,
                tradeDataset::size,
                schemaTypeMap::get
        );

        if (result == null) return null;

        if (!(result instanceof Number)) {
            throw new AstException("Non-numeric result");
        }

        return ((Number) result).doubleValue();
    }
}
