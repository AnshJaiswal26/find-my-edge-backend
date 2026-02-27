package com.example.find_my_edge.analytics.ast.reducer.runner;

import com.example.find_my_edge.analytics.ast.context.EvaluationContext;
import com.example.find_my_edge.analytics.ast.evaluator.AstEvaluator;
import com.example.find_my_edge.analytics.ast.model.AstNode;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import com.example.find_my_edge.common.config.AstConfig;
import org.springframework.stereotype.Component;


@Component("PURE")
public class BaseReducerRunner implements ReducerRunnerStrategy {

    @Override
    public Object run(Reducer reducer, AstNode fn, EvaluationContext ctx, AstEvaluator evaluator) {

        int n = fn.getArgs() != null ? fn.getArgs().size() : 0;

        Object state = reducer.init(n);

        if (fn.getArgs() == null) {
            return reducer.result(state);
        }

        for (AstNode arg : fn.getArgs()) {

            Object value = evaluator.evaluate(arg, ctx);

            // IMPORTANT: your reducer expects Object[]
            boolean shouldContinue = reducer.step(state, new Object[]{value});

            // optional early exit (nice design 👌)
            if (!shouldContinue) break;
        }

        return reducer.result(state);
    }
}