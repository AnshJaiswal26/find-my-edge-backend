package com.example.find_my_edge.analytics.ast.reducer.logical;

import com.example.find_my_edge.analytics.ast.context.EvaluationContext;
import com.example.find_my_edge.analytics.ast.evaluator.AstEvaluator;
import com.example.find_my_edge.analytics.ast.function.annotation.ArgType;
import com.example.find_my_edge.analytics.ast.function.annotation.FunctionMeta;
import com.example.find_my_edge.analytics.ast.function.enums.ExecutionMode;
import com.example.find_my_edge.analytics.ast.function.enums.FunctionMode;
import com.example.find_my_edge.analytics.ast.function.enums.FunctionType;
import com.example.find_my_edge.analytics.ast.model.AstNode;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

import java.util.List;

@FunctionMeta(
        argTypes = {"boolean", "any", "any"},
        semanticArgs = {
                @ArgType({"boolean"}),
                @ArgType({"any"}),
                @ArgType({"any"})
        },
        returnType = "any",
        semanticReturn = "any",
        signature = "IF(cond, trueExpr, falseExpr)",
        description = "Conditional expression",
        modes = {FunctionMode.BASE, FunctionMode.AGGREGATE, FunctionMode.WINDOW}
)
@Component
public class IfFunction implements Reducer {

    @Override
    public FunctionType getType() {
        return FunctionType.PURE;
    }

    @Override
    public ExecutionMode getExecutionMode() {
        return ExecutionMode.AST;
    }

    @Override
    public String getName() {
        return "IF";
    }

    // ---------- PURE EXECUTION ----------

    @Override
    public Object execute(AstNode fn, EvaluationContext ctx, AstEvaluator evaluator) {

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