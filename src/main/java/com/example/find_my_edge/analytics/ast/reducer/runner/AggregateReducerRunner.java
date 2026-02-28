package com.example.find_my_edge.analytics.ast.reducer.runner;

import com.example.find_my_edge.analytics.ast.context.EvaluationContext;
import com.example.find_my_edge.analytics.ast.evaluator.AstEvaluator;
import com.example.find_my_edge.analytics.ast.exception.AstExecutionException;
import com.example.find_my_edge.analytics.ast.function.enums.FunctionType;
import com.example.find_my_edge.analytics.ast.model.AstNode;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component("AGGREGATE")
public class AggregateReducerRunner implements ReducerRunnerStrategy {

    @Override
    public Object run(Reducer reducer, AstNode node, EvaluationContext ctx, AstEvaluator evaluator) {

        List<AstNode> args = node.getArgs();

        Object state = reducer.init();

        if (state == null) {
            throw new AstExecutionException(
                    "[Aggregation Execution Error]",
                    "Reducer returned null state for: " + node.getFn()
            );
        }

        Integer total = ctx.getTradeCount();
        if (total == null) {
            throw new AstExecutionException(
                    "[Aggregation Execution Error]",
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

//        System.out.println(reducer.result(state));
        return reducer.result(state);
    }
}