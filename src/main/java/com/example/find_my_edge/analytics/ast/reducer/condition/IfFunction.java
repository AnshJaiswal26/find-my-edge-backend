package com.example.find_my_edge.analytics.ast.reducer.condition;

import com.example.find_my_edge.analytics.ast.context.EvaluationContext;
import com.example.find_my_edge.analytics.ast.evaluator.AstEvaluator;
import com.example.find_my_edge.analytics.ast.model.AstNode;
import java.util.List;

public class IfFunction {

    public Object apply(AstNode fn, EvaluationContext ctx, AstEvaluator evaluator) {
        List<AstNode> args = fn.getArgs();

        AstNode condExpr = args.get(0);
        AstNode trueExpr = args.get(1);
        AstNode falseExpr = args.get(2);

        Object condResult = evaluator.evaluate(condExpr, ctx);

        boolean condition = condResult != null && (Boolean) condResult;

        AstNode selectedExpr = condition ? trueExpr : falseExpr;

        return evaluator.evaluate(selectedExpr, ctx);
    }
}