package com.example.find_my_edge.analytics.ast.reducer.runner;

import com.example.find_my_edge.analytics.ast.context.EvaluationContext;
import com.example.find_my_edge.analytics.ast.evaluator.AstEvaluator;
import com.example.find_my_edge.analytics.ast.model.AstNode;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("WINDOW")
public class WindowReducerRunner implements ReducerRunnerStrategy {


    @Override
    public Object run(Reducer reducer, AstNode fn, EvaluationContext ctx, AstEvaluator evaluator) {

        List<AstNode> args = fn.getArgs();
        if (args == null || args.isEmpty()) return null;

        // 👉 last arg = window size
        AstNode nExpr = args.getLast();
        Object nVal = evaluator.evaluate(nExpr, ctx);

        int n = (nVal instanceof Number)
                ? (int) Math.floor(((Number) nVal).doubleValue())
                : 0;

        Object state = reducer.init(n);
        if (state == null) return null;

        // 👉 remaining args = values
        List<AstNode> valueExprs = args.subList(0, args.size() - 1);

        for (int i = ctx.getWindowStartIndex(); i >= 0; i--) {

            ctx.setTradeIndex(i);

            // evaluate all expressions
            Object[] evaluated = valueExprs.stream()
                                           .map(expr -> evaluator.evaluate(expr, ctx))
                                           .toArray();

            boolean cont = reducer.step(state, evaluated);

            if (!cont) break;
        }

        return reducer.result(state);
    }
}