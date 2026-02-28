package com.example.find_my_edge.analytics.ast.reducer.math;

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

@FunctionMeta(
        argTypes = {"number", "number", "number"},
        semanticArgs = {
                @ArgType({"number"}),
                @ArgType({"number"}),
                @ArgType({"number"})
        },
        returnType = "number",
        semanticReturn = "number",
        signature = "CLAMP(expr, min, max)",
        description = "Clamp value to range",
        modes = {FunctionMode.BASE, FunctionMode.AGGREGATE, FunctionMode.WINDOW}
)
@Component
public class ClampFunction implements Reducer {

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
        return "CLAMP";
    }

    // ---------- PURE EXECUTION ----------

    @Override
    public Object execute(AstNode fn, EvaluationContext ctx, AstEvaluator evaluator) {

        AstNode valueExpr = fn.getArgs().get(0);
        AstNode minExpr = fn.getArgs().get(1);
        AstNode maxExpr = fn.getArgs().get(2);

        Object value = evaluator.evaluate(valueExpr, ctx);
        Object min = evaluator.evaluate(minExpr, ctx);
        Object max = evaluator.evaluate(maxExpr, ctx);

        if (value == null || min == null || max == null) return null;

        double v = ((Number) value).doubleValue();
        double minVal = ((Number) min).doubleValue();
        double maxVal = ((Number) max).doubleValue();

        return Math.min(Math.max(v, minVal), maxVal);
    }
}