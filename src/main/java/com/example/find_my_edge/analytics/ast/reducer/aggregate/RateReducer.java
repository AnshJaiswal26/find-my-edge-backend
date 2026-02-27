package com.example.find_my_edge.analytics.ast.reducer.aggregate;

import com.example.find_my_edge.analytics.ast.function.ArgType;
import com.example.find_my_edge.analytics.ast.function.ExecutionMode;
import com.example.find_my_edge.analytics.ast.function.FunctionMeta;
import com.example.find_my_edge.analytics.ast.function.FunctionType;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@FunctionMeta(
        argTypes = {"boolean"},
        semanticArgs = {
                @ArgType({"boolean"})
        },
        returnType = "number",
        semanticReturn = "number",
        signature = "RATE(condition)",
        description = "Percentage of rows where condition is true"
)
@Component
public class RateReducer implements Reducer {

    // ---------- STATE ----------
    private static class State {
        int total;
        int trueCount;

        State() {
            this.total = 0;
            this.trueCount = 0;
        }
    }

    @Override
    public FunctionType getType() {
        return FunctionType.AGGREGATE;
    }

    @Override
    public ExecutionMode getExecutionMode() {
        return ExecutionMode.NATIVE;
    }

    @Override
    public String getName() {
        return "RATE";
    }

// ---------- EXECUTION ----------

    @Override
    public Object init(int n) {
        return new State(); // n not used
    }

    @Override
    public Boolean step(Object stateObj, Object[] args) {
        if (stateObj == null || args == null || args.length == 0) return true;

        State state = (State) stateObj;
        Object condObj = args[0];

        state.total++;

        if (Boolean.TRUE.equals(condObj)) {
            state.trueCount++;
        }

        return true; // process all rows
    }

    @Override
    public Object result(Object stateObj) {
        if (stateObj == null) return null;

        State state = (State) stateObj;

        return state.total == 0
               ? 0.0
               : (double) state.trueCount / state.total;
    }
}