package com.example.find_my_edge.analytics.ast.reducer.schema;

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

@Component
@FunctionMeta(
        argTypes = {"number", "boolean"},
        semanticArgs = {
                @ArgType({"number"}),
                @ArgType({"boolean"})
        },
        returnType = "number",
        semanticReturn = "number",
        signature = "CUMSUM_RESET(expr, condition)",
        description = "Accumulate expr but reset when condition is true",
        modes = {FunctionMode.WINDOW}
)
public class CumSumResetFunction implements Reducer {

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
        return "CUMSUM_RESET";
    }

    @Override
    public Object execute(AstNode fn, EvaluationContext ctx, AstEvaluator evaluator) {

        AstNode valueExpr = fn.getArgs().get(0);
        AstNode condExpr = fn.getArgs().get(1);

        Object valueObj = evaluator.evaluate(valueExpr, ctx);
        Object condObj = evaluator.evaluate(condExpr, ctx);

        double value = valueObj != null ? ((Number) valueObj).doubleValue() : 0.0;
        boolean shouldReset = condObj != null && (Boolean) condObj;

        Object prev = ctx.getPrevValue();

        if (shouldReset || prev == null) {
            return value; // start new segment
        }

        return ((Number) prev).doubleValue() + value; // continue accumulation
    }
}