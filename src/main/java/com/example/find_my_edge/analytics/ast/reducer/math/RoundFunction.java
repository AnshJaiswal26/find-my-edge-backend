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
        argTypes = {"number", "number"},
        semanticArgs = {
                @ArgType({"number"}),
                @ArgType({"number"})
        },
        returnType = "number",
        semanticReturn = "number",
        signature = "ROUND(expr, digits)",
        description = "Round to N decimal places",
        modes = {FunctionMode.BASE, FunctionMode.AGGREGATE, FunctionMode.WINDOW}
)
@Component
public class RoundFunction implements Reducer {

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
        return "ROUND";
    }

    // ---------- PURE EXECUTION ----------

    @Override
    public Object execute(AstNode fn, EvaluationContext ctx, AstEvaluator evaluator) {

        AstNode valueExpr = fn.getArgs().get(0);
        AstNode digitsExpr = fn.getArgs().size() > 1 ? fn.getArgs().get(1) : null;

        Object value = evaluator.evaluate(valueExpr, ctx);
        if (value == null) return null;

        Object digitsVal = digitsExpr != null ? evaluator.evaluate(digitsExpr, ctx) : 0;

        int d = (digitsVal != null) ? ((Number) digitsVal).intValue() : 0;
        double factor = Math.pow(10, d);

        double v = ((Number) value).doubleValue();

        return Math.round(v * factor) / factor;
    }
}