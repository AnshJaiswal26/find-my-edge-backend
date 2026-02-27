package com.example.find_my_edge.analytics.ast.reducer.base;

import com.example.find_my_edge.analytics.ast.function.ArgType;
import com.example.find_my_edge.analytics.ast.function.ExecutionMode;
import com.example.find_my_edge.analytics.ast.function.FunctionMeta;
import com.example.find_my_edge.analytics.ast.function.FunctionType;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@FunctionMeta(
        argTypes = {"number"},
        semanticArgs = {
                @ArgType({"number"})
        },
        returnType = "number",
        semanticReturn = "number",
        signature = "ABS(expr)",
        description = "Absolute value"
)
@Component
public class AbsReducer implements Reducer {

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
        return "ABS";
    }

    // ---------- EXECUTION ----------

    @Override
    public Object init(int n) {
        return new State();
    }

    @Override
    public Boolean step(Object stateObj, Object[] args) {
        State state = (State) stateObj;

        Object val = args[0];
        if (val == null) return true;

        state.value = Math.abs(((Number) val).doubleValue());

        return false; // single-step function
    }

    @Override
    public Object result(Object stateObj) {
        return ((State) stateObj).value;
    }
}