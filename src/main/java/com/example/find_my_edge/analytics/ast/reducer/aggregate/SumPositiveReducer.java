package com.example.find_my_edge.analytics.ast.reducer.aggregate;

import com.example.find_my_edge.analytics.ast.function.annotation.ArgType;
import com.example.find_my_edge.analytics.ast.function.annotation.FunctionMeta;
import com.example.find_my_edge.analytics.ast.function.enums.ExecutionMode;
import com.example.find_my_edge.analytics.ast.function.enums.FunctionMode;
import com.example.find_my_edge.analytics.ast.function.enums.FunctionType;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@FunctionMeta(
        argTypes = {"number"},
        semanticArgs = {
                @ArgType({"number", "duration"})
        },
        returnType = "number",
        semanticReturn = "same",
        signature = "SUM_POSITIVE(expr)",
        description = "Sum of values greater than 0",
        modes = {FunctionMode.AGGREGATE}
)
@Component
public class SumPositiveReducer implements Reducer {

    // ---------- STATE ----------
    private static class State {
        double sum = 0.0;
    }

    @Override
    public ExecutionMode getExecutionMode() {
        return ExecutionMode.AST;
    }

    @Override
    public FunctionType getType() {
        return FunctionType.AGGREGATE;
    }

    @Override
    public String getName() {
        return "SUM_POSITIVE";
    }



    // ---------- EXECUTION ----------

    @Override
    public Object init() {
        return new State();
    }

    @Override
    public Boolean step(Object stateObj, Object[] args) {
        if (stateObj == null || args == null || args.length < 1) return true;

        State s = (State) stateObj;
        Object valueObj = args[0];

        if (valueObj instanceof Number) {
            double v = ((Number) valueObj).doubleValue();
            if (v > 0) {
                s.sum += v;
            }
        }

        return true;
    }

    @Override
    public Object result(Object stateObj) {
        if (stateObj == null) return null;

        State s = (State) stateObj;
        return s.sum;
    }
}