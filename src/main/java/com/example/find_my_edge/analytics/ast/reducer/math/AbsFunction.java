package com.example.find_my_edge.analytics.ast.reducer.math;

import com.example.find_my_edge.analytics.ast.context.EvaluationContext;
import com.example.find_my_edge.analytics.ast.evaluator.AstEvaluator;
import com.example.find_my_edge.analytics.ast.function.annotation.ArgType;
import com.example.find_my_edge.analytics.ast.function.enums.ExecutionMode;
import com.example.find_my_edge.analytics.ast.function.annotation.FunctionMeta;
import com.example.find_my_edge.analytics.ast.function.enums.FunctionMode;
import com.example.find_my_edge.analytics.ast.function.enums.FunctionType;
import com.example.find_my_edge.analytics.ast.model.AstNode;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@Component
@FunctionMeta(
        argTypes = {"number"},
        semanticArgs = {
                @ArgType({"number"})
        },
        returnType = "number",
        semanticReturn = "number",
        signature = "ABS(expr)",
        description = "Absolute value",
        modes = {FunctionMode.BASE, FunctionMode.AGGREGATE, FunctionMode.WINDOW}
)
public class AbsFunction implements Reducer {

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
        return "ABS";
    }

    @Override
    public Object execute(AstNode fn, EvaluationContext ctx, AstEvaluator evaluator) {

        AstNode expr = fn.getArgs().getFirst();

        Object value = evaluator.evaluate(expr, ctx);
        if (value == null) return null;

        return Math.abs(((Number) value).doubleValue());
    }
}