package com.example.find_my_edge.analytics.ast.reducer.runner;

import com.example.find_my_edge.analytics.ast.context.EvaluationContext;
import com.example.find_my_edge.analytics.ast.evaluator.AstEvaluator;
import com.example.find_my_edge.analytics.ast.exception.AstExecutionException;
import com.example.find_my_edge.analytics.ast.model.AstNode;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@Component("NATIVE_AGG")
public class NativeAggregateReducerRunner implements ReducerRunnerStrategy {

    @Override
    public Object run(Reducer reducer, AstNode fn, EvaluationContext ctx, AstEvaluator evaluator) {

        // ✅ reducer must define field
        String field = reducer.getField();
        if (field == null) {
            throw new AstExecutionException(
                    "[Native Aggregation Execution Error]",
                    "field not found in reducer '" + reducer.getName() + "'"
            );
        }

        Object state = reducer.init(); // or 0 depending on your reducer design
        if (state == null) return null;

        Integer total = ctx.getTradeCount();
        if (total == null) return null;

        for (int i = 0; i < total; i++) {

            // direct value access (no AST evaluation)
            Object value = ctx.getTradeValue(i, field);

            if (value == null) continue;

            boolean cont = reducer.step(state, new Object[]{value});

            if (!cont) break;
        }

        return reducer.result(state);
    }
}