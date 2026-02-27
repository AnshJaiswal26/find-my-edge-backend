package com.example.find_my_edge.analytics.ast.reducer.base;

import com.example.find_my_edge.analytics.ast.function.ArgType;
import com.example.find_my_edge.analytics.ast.function.ExecutionMode;
import com.example.find_my_edge.analytics.ast.function.FunctionMeta;
import com.example.find_my_edge.analytics.ast.function.FunctionType;
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
        description = "Clamp value to range"
)
@Component
public class ClampReducer implements Reducer {

    static class State {
        Double value;
    }

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


    // ---------- EXECUTION ----------

    @Override
    public Object init() {
        return new State();
    }

    @Override
    public Boolean step(Object stateObj, Object[] args) {
        State state = (State) stateObj;

        Object value = args[0];
        Object min = args[1];
        Object max = args[2];

        if (value == null || min == null || max == null) return true;

        double v = ((Number) value).doubleValue();
        double minVal = ((Number) min).doubleValue();
        double maxVal = ((Number) max).doubleValue();

        state.value = Math.min(Math.max(v, minVal), maxVal);

        return false; // single-step
    }

    @Override
    public Object result(Object stateObj) {
        return ((State) stateObj).value;
    }
}