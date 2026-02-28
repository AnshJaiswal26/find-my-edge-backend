package com.example.find_my_edge.analytics.ast.reducer.window;

import com.example.find_my_edge.analytics.ast.function.annotation.ArgType;
import com.example.find_my_edge.analytics.ast.function.enums.ExecutionMode;
import com.example.find_my_edge.analytics.ast.function.annotation.FunctionMeta;
import com.example.find_my_edge.analytics.ast.function.enums.FunctionMode;
import com.example.find_my_edge.analytics.ast.function.enums.FunctionType;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import org.springframework.stereotype.Component;


@FunctionMeta(
        argTypes = {"boolean", "number"},
        semanticArgs = {
                @ArgType({"boolean"}),
                @ArgType({"number"})
        },
        returnType = "number",
        semanticReturn = "number",
        signature = "COUNT_IF_N(condition, n)",
        description = "Count of rows in last N where condition is true",
        modes = {FunctionMode.WINDOW}
)
@Component
public class CountIfNReducer implements Reducer {

    private final SumNReducer sumN = new SumNReducer();

    @Override
    public FunctionType getType() {
        return FunctionType.WINDOW;
    }

    @Override
    public String getName() {
        return "COUNT_IF_N";
    }

    @Override
    public ExecutionMode getExecutionMode() {
        return ExecutionMode.AST;
    }

    // ---------- EXECUTION (DELEGATION) ----------

    @Override
    public Object init(int n) {
        return sumN.init(n);
    }

    @Override
    public Boolean step(Object state, Object[] args) {
        if (args == null || args.length == 0) return true;

        Object conditionObj = args[0];

        // convert boolean → 1 or 0
        double value = conditionObj != null && Boolean.TRUE.equals(conditionObj) ? 1.0 : 0.0;

        return sumN.step(state, new Object[]{value});
    }

    @Override
    public Object result(Object state) {
        return sumN.result(state);
    }
}