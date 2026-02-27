package com.example.find_my_edge.analytics.service.impl;

import com.example.find_my_edge.analytics.ast.context.SchemaType;
import com.example.find_my_edge.analytics.ast.enums.ComputationMode;
import com.example.find_my_edge.analytics.ast.exception.AstException;
import com.example.find_my_edge.analytics.ast.executor.AggregateExecutor;
import com.example.find_my_edge.analytics.ast.executor.RowSequenceExecutor;
import com.example.find_my_edge.analytics.ast.mapper.AstNodeMapper;
import com.example.find_my_edge.analytics.ast.model.AstNode;
import com.example.find_my_edge.analytics.ast.model.AstResult;
import com.example.find_my_edge.analytics.ast.parser.AstPipeline;
import com.example.find_my_edge.analytics.ast.util.DependencyResolver;
import com.example.find_my_edge.analytics.service.ComputeService;
import com.example.find_my_edge.domain.schema.enums.ComputeMode;
import com.example.find_my_edge.domain.schema.enums.SchemaSource;
import com.example.find_my_edge.domain.schema.enums.SemanticType;
import com.example.find_my_edge.domain.schema.model.Schema;
import com.example.find_my_edge.domain.schema.model.SchemaBundle;
import com.example.find_my_edge.domain.schema.service.SchemaService;
import com.example.find_my_edge.domain.trade.model.Trade;
import com.example.find_my_edge.domain.trade.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
public class ComputeServiceImpl implements ComputeService {

    private final AggregateExecutor aggregateExecutor;
    private final RowSequenceExecutor rowSequenceExecutor;
    private final DependencyResolver dependencyResolver;

    private final SchemaService schemaService;
    private final TradeService tradeService;

    private final AstPipeline astPipeline;

    //  Inject shared executor (defined as @Bean)
    private final ExecutorService computeExecutor;


    /**
     * Build trade context (raw + computed fields)
     */
    @Override
    public Map<String, Map<String, Double>> getTradeContext(
            Map<String, Schema> schemasById,
            List<Trade> trades
    ) {

        Map<String, Map<String, Double>> tradeContext = new HashMap<>();

        System.out.println(schemasById);
        //  Preload RAW fields
        for (Trade trade : trades) {
            Map<String, Double> ctx = new HashMap<>();


            trade.getValues()
                 .forEach((k, v) -> {
                     Schema schema = schemasById.get(k);
                     System.out.println(schema);

                     if (schema == null) return;

                     System.out.println("k: " + k + "v: " + v);
                     if (v != null) {
                         if (v instanceof Number n)
                             ctx.put(k, n.doubleValue());

                         if (v instanceof Boolean b)
                             ctx.put(k, Boolean.TRUE.equals(b) ? 1.0 : 0.0);
                     }
                 });

            tradeContext.put(trade.getId(), ctx);
        }

        //  Build dependency graph
        Map<String, List<String>> dependsOnMap =
                dependencyResolver.buildDependencyMap(schemasById.values());

        List<String> executionOrder =
                dependencyResolver.resolveExecutionOrder(dependsOnMap);

        //  Precompute SchemaType
        Map<String, SchemaType> schemaTypeMap = new HashMap<>();
        for (Schema s : schemasById.values()) {
            schemaTypeMap.put(
                    s.getId(),
                    new SchemaType(s.getDisplay().getFormat(), s.getType().toString())
            );
        }

        //  Row computation
        for (String schemaId : executionOrder) {

            Schema schema = schemasById.get(schemaId);

            if (schema.getAst() == null) continue;

            AstNode astNode = AstNodeMapper.toNode(schema.getAst());

            rowSequenceExecutor.execute(
                    astNode,
                    schema.getId(),
                    0,
                    schema.getInitialValue(),
                    (index, key, val) -> {
                        if (index < 0 || val == null) return;

                        String tradeId = trades.get(index).getId();
                        if (tradeId == null) return;

                        tradeContext
                                .computeIfAbsent(tradeId, k -> new HashMap<>())
                                .put(key, (Double) val);
                    },
                    (index, key) -> {
                        if (index < 0) return null;

                        String tradeId = trades.get(index).getId();
                        if (tradeId == null) return null;

                        Map<String, Double> ctx = tradeContext.get(tradeId);
                        return ctx != null ? ctx.get(key) : null;
                    },
                    trades::size,
                    schemaTypeMap::get,
                    schema.getMode() == ComputeMode.ROW
                    ? ComputationMode.BASE
                    : ComputationMode.WINDOW
            );
        }

        return tradeContext;
    }

    /**
     * Compute aggregates in parallel
     */
    @Override
    public Map<String, Double> computeAggregateFromFormulas(Map<String, String> formulas) {

        // 🔹 Load base data
        SchemaBundle schemaBundle = schemaService.getAll();
        Map<String, Schema> schemasById = schemaBundle.getSchemasById();
        List<Trade> trades = tradeService.getAll();

        // 🔹 Build trade context (uses stored schemas only)
        Map<String, Map<String, Double>> tradeContext =
                getTradeContext(schemasById, trades);

        // 🔹 Precompute tradeIds
        List<String> tradeIds = trades.stream()
                                      .map(Trade::getId)
                                      .toList();

        // 🔹 Precompute SchemaType
        Map<String, SchemaType> schemaTypeMap = new HashMap<>();
        for (Schema s : schemasById.values()) {
            schemaTypeMap.put(
                    s.getId(),
                    new SchemaType(s.getDisplay().getFormat(), s.getType().toString())
            );
        }

        // 🔥 STEP 1: Build & Validate ASTs (IMPORTANT)
        Map<String, AstNode> astMap = new HashMap<>();

        for (Map.Entry<String, String> entry : formulas.entrySet()) {
            String key = entry.getKey();
            String formula = entry.getValue();

            if (formula == null || formula.isBlank()) {
                throw new AstException("Formula cannot be empty for key: " + key);
            }

            // ✅ Build AST (this also validates)
            AstResult astResult = astPipeline.buildAst(formula, "");

            astMap.put(key, astResult.getAstNode());
        }

        // 🔥 STEP 2: Parallel Execution
        Map<String, Future<Double>> futures = new HashMap<>();

        for (Map.Entry<String, AstNode> entry : astMap.entrySet()) {

            String key = entry.getKey();
            AstNode astNode = entry.getValue();

            futures.put(
                    key, computeExecutor.submit(() -> {

                        return (Double) aggregateExecutor.execute(
                                astNode,
                                (index, schemaKey) -> {
                                    if (index < 0) return null;

                                    String tradeId = tradeIds.get(index);
                                    if (tradeId == null) return null;

                                    Map<String, Double> ctx = tradeContext.get(tradeId);
                                    return ctx != null ? ctx.get(schemaKey) : null;
                                },
                                trades::size,
                                schemaTypeMap::get
                        );
                    })
            );
        }

        // 🔥 STEP 3: Collect Results
        Map<String, Double> result = new HashMap<>();

        for (Map.Entry<String, Future<Double>> entry : futures.entrySet()) {
            try {
                result.put(entry.getKey(), entry.getValue().get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new AstException("Thread interrupted", e);
            } catch (ExecutionException e) {
                throw new AstException(
                        "Error computing formula: " + entry.getKey(),
                        e.getCause()
                );
            }
        }

        return result;
    }
}