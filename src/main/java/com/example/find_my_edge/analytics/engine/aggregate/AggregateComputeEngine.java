package com.example.find_my_edge.analytics.engine.aggregate;

import com.example.find_my_edge.analytics.ast.context.SchemaType;
import com.example.find_my_edge.analytics.ast.exception.AstException;
import com.example.find_my_edge.analytics.ast.exception.AstExecutionException;
import com.example.find_my_edge.analytics.ast.executor.AggregateExecutor;
import com.example.find_my_edge.analytics.ast.function.enums.FunctionMode;
import com.example.find_my_edge.analytics.ast.mapper.AstNodeMapper;
import com.example.find_my_edge.analytics.ast.model.AstNode;
import com.example.find_my_edge.analytics.ast.parser.AstPipeline;
import com.example.find_my_edge.analytics.ast.util.SchemaTypeResolver;
import com.example.find_my_edge.analytics.engine.dataSet.TradeDataset;
import com.example.find_my_edge.common.config.uiconfigs.AstConfig;
import com.example.find_my_edge.schema.model.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
@RequiredArgsConstructor
public class AggregateComputeEngine {

    private final AggregateExecutor aggregateExecutor;
    private final AstPipeline astPipeline;

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
