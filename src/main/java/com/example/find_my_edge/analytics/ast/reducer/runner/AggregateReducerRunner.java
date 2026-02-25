package com.example.find_my_edge.analytics.ast.reducer.runner;

import com.example.find_my_edge.analytics.ast.context.EvaluationContext;
import com.example.find_my_edge.analytics.ast.evaluator.AstEvaluator;
import com.example.find_my_edge.analytics.ast.exception.AstExecutionException;
import com.example.find_my_edge.analytics.ast.model.AstNode;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("GLOBAL") // also reuse for RATIO if needed
public class AggregateReducerRunner implements ReducerRunnerStrategy {

    @Override
    public Object run(Reducer reducer, AstNode fn, EvaluationContext ctx, AstEvaluator evaluator) {

        List<AstNode> args = fn.getArgs();

        Object state = reducer.init(args != null ? args.size() : 0);

        if (state == null){
            throw new AstExecutionException(
                    "Reducer returned null state for: " + fn.getFn()
            );
        }

        Integer total = ctx.getTradeCount();
        if (total == null) {
            throw new AstExecutionException(
                    "Trade count is missing in evaluation context"
            );
        }

        int originalIndex = ctx.getTradeIndex(); // 🔥 restore later

        for (int i = 0; i < total; i++) {

            ctx.setTradeIndex(i);

            Object[] evaluated = (args == null)
                                 ? new Object[0]
                                 : args.stream()
                                       .map(expr -> evaluator.evaluate(expr, ctx))
                                       .toArray();

            boolean cont = reducer.step(state, evaluated);

            // optional early exit (consistent with your design)
            if (!cont) break;
        }

        ctx.setTradeIndex(originalIndex); // ✅ restore context

        return reducer.result(state);
    }
}