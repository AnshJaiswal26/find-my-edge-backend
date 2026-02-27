package com.example.find_my_edge.analytics.ast.context;

import com.example.find_my_edge.analytics.ast.evaluator.AstEvaluator;
import lombok.*;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefaultEvaluationContext implements EvaluationContext {

    private BiFunction<Integer, String, Object> getTradeValue;
    private Supplier<Integer> getTradeCount;
    private Function<String, SchemaType> getSchemaType;

    private int tradeIndex = 0;

    private int windowStartIndex = 0;

    private Object prevValue = null;

    private AstEvaluator evaluator;

    // ---------- interface methods ----------

    @Override
    public Object getKeyValue(String key) {
        return getTradeValue.apply(tradeIndex, key);
    }

    @Override
    public Object getTradeValue(int index, String key) {
        return getTradeValue.apply(index, key);
    }

    @Override
    public Integer getTradeCount() {
        return getTradeCount.get();
    }

    @Override
    public Integer getTradeIndex() {
        return tradeIndex;
    }

    @Override
    public SchemaType getSchemaType(String key) {
        return getSchemaType.apply(key);
    }

    @Override
    public Integer getWindowStartIndex() {
        return windowStartIndex; // or custom logic if needed
    }
}