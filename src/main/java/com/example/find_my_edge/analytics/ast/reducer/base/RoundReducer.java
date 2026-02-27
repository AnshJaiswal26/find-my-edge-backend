package com.example.find_my_edge.analytics.ast.reducer.base;

import com.example.find_my_edge.analytics.ast.function.ArgType;
import com.example.find_my_edge.analytics.ast.function.ExecutionMode;
import com.example.find_my_edge.analytics.ast.function.FunctionMeta;
import com.example.find_my_edge.analytics.ast.function.FunctionType;
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
        description = "Round to N decimal places"
)
@Component
public class RoundReducer implements Reducer {

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
        return "ROUND";
    }


    // ---------- EXECUTION ----------

    @Override
    public Object init(int n) {
        return new State();
    }

    @Override
    public Boolean step(Object stateObj, Object[] args) {
        State state = (State) stateObj;

        Object value = args[0];
        Object decimals = args.length > 1 ? args[1] : 0;

        if (value == null) return true;

        double v = ((Number) value).doubleValue();
        int d = (decimals != null) ? ((Number) decimals).intValue() : 0;

        double factor = Math.pow(10, d);

        state.value = Math.round(v * factor) / factor;

        return false; // single-step reducer
    }

    @Override
    public Object result(Object stateObj) {
        return ((State) stateObj).value;
    }
}