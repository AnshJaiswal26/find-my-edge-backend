package com.example.find_my_edge.analytics.ast.reducer.runner;

import com.example.find_my_edge.analytics.ast.context.EvaluationContext;
import com.example.find_my_edge.analytics.ast.evaluator.AstEvaluator;
import com.example.find_my_edge.analytics.ast.model.AstNode;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import com.example.find_my_edge.common.config.AstConfig;
import org.springframework.stereotype.Component;

@Component("NATIVE_AGG")
public class NativeAggregateReducerRunner implements ReducerRunnerStrategy {

    @Override
    public Object run(Reducer reducer, AstNode fn, EvaluationContext ctx, AstEvaluator evaluator) {

        // ✅ reducer must define key
        String key = reducer.getKey();
        if (key == null) {
            throw new IllegalStateException(
                    "runNativeAggregateReducer: key not found in reducer '" + reducer.getName() + "'"
            );
        }

        Object state = reducer.init(1); // or 0 depending on your reducer design
        if (state == null) return null;

        Integer total = ctx.getTradeCount();
        if (total == null) return null;

        for (int i = 0; i < total; i++) {

            // direct value access (no AST evaluation)
            Object value = ctx.getTradeValue(i, key);

            if (value == null) continue;

            boolean cont = reducer.step(state, new Object[]{value});

            if (!cont) break;
        }

        return reducer.result(state);
    }
}