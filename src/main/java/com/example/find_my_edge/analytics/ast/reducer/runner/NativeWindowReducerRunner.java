package com.example.find_my_edge.analytics.ast.reducer.runner;

import com.example.find_my_edge.analytics.ast.context.EvaluationContext;
import com.example.find_my_edge.analytics.ast.evaluator.AstEvaluator;
import com.example.find_my_edge.analytics.ast.exception.AstExecutionException;
import com.example.find_my_edge.analytics.ast.model.AstNode;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("NATIVE_WINDOW")
public class NativeWindowReducerRunner implements ReducerRunnerStrategy {

    @Override
    public Object run(Reducer reducer, AstNode fn, EvaluationContext ctx, AstEvaluator evaluator) {

        List<AstNode> args = fn.getArgs();
        if (args == null || args.isEmpty()) return null;

        // ✅ reducer must define field
        String field = reducer.getField();
        if (field == null) {
            throw new AstExecutionException(
                    "[Native Window Execution Error]",
                    "field not found in reducer '" + reducer.getName() + "'"
            );
        }

        // 👉 first arg = window size (NOTE: your JS uses args[0])
        Object nVal = evaluator.evaluate(args.getFirst(), ctx);
        int windowSize = (nVal instanceof Number)
                         ? (int) Math.floor(((Number) nVal).doubleValue())
                         : 0;

        if (windowSize <= 0) return null;

        Object state = reducer.init(windowSize);
        if (state == null) return null;

        int originalIndex = ctx.getTradeIndex(); // 🔥 restore later

        int count = 0;

        for (int i = ctx.getWindowStartIndex(); i >= 0 && count < windowSize; i--) {

            // 🚀 no AST eval — direct access
            Object value = ctx.getTradeValue(i, field);

            boolean cont = reducer.step(state, new Object[]{value});

            if (!cont) break;

            count++;
        }

        ctx.setTradeIndex(originalIndex); // ✅ restore

        return reducer.result(state);
    }
}