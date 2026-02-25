package com.example.find_my_edge.analytics.ast.executor;

import com.example.find_my_edge.analytics.ast.context.DefaultEvaluationContext;
import com.example.find_my_edge.analytics.ast.context.SchemaType;
import com.example.find_my_edge.analytics.ast.evaluator.AstEvaluator;
import com.example.find_my_edge.analytics.ast.model.AstNode;
import com.example.find_my_edge.common.config.AstConfig;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
public class AggregateExecutor {

    private final AstEvaluator evaluator;

    public AggregateExecutor(AstEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    public Object execute(
            @NonNull AstNode ast,
            @NonNull BiFunction<Integer, String, Object> getTradeValue,
            @NonNull Supplier<Integer> getTradeCount,
            @NonNull Function<String, SchemaType> getSchemaType
    ) {


        DefaultEvaluationContext ctx = new DefaultEvaluationContext();

        ctx.setGetTradeValue(getTradeValue);
        ctx.setGetTradeCount(getTradeCount);
        ctx.setGetSchemaType(getSchemaType);

        ctx.setTradeIndex(0);


        return evaluator.evaluate(ast, ctx);
    }
}