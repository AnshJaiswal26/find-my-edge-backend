package com.example.find_my_edge.analytics.ast.reducer.context;

import com.example.find_my_edge.analytics.ast.function.ExecutionMode;
import com.example.find_my_edge.analytics.ast.function.FunctionMeta;
import com.example.find_my_edge.analytics.ast.function.FunctionType;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@FunctionMeta(
        argTypes = {},
        semanticArgs = {},
        returnType = "number",
        semanticReturn = "number",
        signature = "WIN_RATE()",
        description = "Winning trades divided by total trades"
)
@Component
public class WinRateReducer implements Reducer {

    // ---------- STATE ----------
    private static class State {
        int total;
        int wins;

        State() {
            this.total = 0;
            this.wins = 0;
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
        return "WIN_RATE";
    }

    @Override
    public String getKey() {
        return "pnl"; // 👈 important
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

        Object pnlObj = args[0]; // injected via key

        if (pnlObj == null) return true;

        double pnl = ((Number) pnlObj).doubleValue();

        state.total++;

        if (pnl > 0) {
            state.wins++;
        }

        return true; // process all rows
    }

    @Override
    public Object result(Object stateObj) {
        if (stateObj == null) return null;

        State state = (State) stateObj;

        return state.total > 0
               ? ((double) state.wins / state.total) * 100.0
               : null;
    }
}