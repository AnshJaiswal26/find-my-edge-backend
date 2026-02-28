package com.example.find_my_edge.analytics.ast.reducer.duration;

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
        argTypes = {"number"},
        semanticArgs = {
                @ArgType({"number"})
        },
        returnType = "number",
        semanticReturn = "duration",
        signature = "MINUTES(n)",
        description = "Convert minutes to duration",
        modes = {FunctionMode.BASE, FunctionMode.AGGREGATE, FunctionMode.WINDOW}
)
@Component
public class MinutesFunction implements Reducer {

    private static final int SECONDS_IN_MINUTE = 60;

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
        return "MINUTES";
    }

    // ---------- PURE EXECUTION ----------

    @Override
    public Object execute(AstNode fn, EvaluationContext ctx, AstEvaluator evaluator) {

        AstNode valueExpr = fn.getArgs().getFirst();

        Object value = evaluator.evaluate(valueExpr, ctx);
        if (value == null) return null;

        return ((Number) value).doubleValue() * SECONDS_IN_MINUTE;
    }
}