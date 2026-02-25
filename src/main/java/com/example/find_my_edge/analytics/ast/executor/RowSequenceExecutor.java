package com.example.find_my_edge.analytics.ast.executor;

import com.example.find_my_edge.analytics.ast.context.DefaultEvaluationContext;
import com.example.find_my_edge.analytics.ast.context.SchemaType;
import com.example.find_my_edge.analytics.ast.enums.ComputationMode;
import com.example.find_my_edge.analytics.ast.evaluator.AstEvaluator;
import com.example.find_my_edge.analytics.ast.model.AstNode;
import com.example.find_my_edge.common.config.AstConfig;
import lombok.NonNull;
import org.apache.logging.log4j.util.TriConsumer;
import org.springframework.stereotype.Service;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
public class RowSequenceExecutor {

    private final AstEvaluator evaluator;

    public RowSequenceExecutor(AstEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    public Object execute(
            @NonNull AstNode ast,
            @NonNull String schemaKey,
            int startIndex,
            Object initialValue,
            @NonNull TriConsumer<Integer, String, Object> setTradeValue,
            @NonNull BiFunction<Integer, String, Object> getTradeValue,
            @NonNull Supplier<Integer> getTradeCount,
            @NonNull Function<String, SchemaType> getSchemaType,
            @NonNull ComputationMode mode
    ) {

        DefaultEvaluationContext ctx = DefaultEvaluationContext
                .builder()
                .tradeIndex(0)
                .windowStartIndex(startIndex)
                .prevValue(initialValue)
                .build();

        ctx.setGetTradeValue(getTradeValue);
        ctx.setGetTradeCount(getTradeCount);
        ctx.setGetSchemaType(getSchemaType);


        boolean isWindow = mode == ComputationMode.WINDOW;

        // preload prevValue for window mode
        if (isWindow && startIndex > 0) {
            Object prev = getTradeValue.apply(startIndex - 1, schemaKey);
            ctx.setPrevValue(prev != null ? prev : initialValue);
        }

        int seqLength = getTradeCount.get();

        for (int i = startIndex; i < seqLength; i++) {

            ctx.setTradeIndex(i);
            ctx.setWindowStartIndex(i); // for window logic

            Object value = evaluator.evaluate(ast, ctx);

            setTradeValue.accept(i, schemaKey, value);

            if (isWindow) {
                ctx.setPrevValue(value);
            }
        }

        return ctx.getPrevValue();
    }
}