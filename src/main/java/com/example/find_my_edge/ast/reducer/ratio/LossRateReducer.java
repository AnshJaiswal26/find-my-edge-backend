package com.example.find_my_edge.ast.reducer.ratio;

import com.example.find_my_edge.ast.function.FunctionType;
import com.example.find_my_edge.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@Component
public class LossRateReducer implements Reducer {

    // ---------- STATE ----------
    private static class State {
        int total;
        int losses;

        State() {
            this.total = 0;
            this.losses = 0;
        }
    }

    // ---------- METADATA ----------

    @Override
    public FunctionType getType() {
        return FunctionType.NATIVE_AGG;
    }

    @Override
    public String getName() {
        return "LOSS_RATE";
    }

    @Override
    public int getArity() {
        return 0; // no args
    }

    @Override
    public String getKey() {
        return "pnl"; // 👈 engine injects pnl
    }

    // ---------- EXECUTION ----------

    @Override
    public Object init(int n) {
        return new State(); // n not used
    }

    @Override
    public boolean step(Object stateObj, Object[] args) {
        if (stateObj == null || args == null || args.length == 0) return true;

        State state = (State) stateObj;

        Object pnlObj = args[0];

        if (pnlObj == null) return true;

        double pnl = ((Number) pnlObj).doubleValue();

        state.total++;

        if (pnl < 0) {
            state.losses++;
        }

        return true; // process all rows
    }

    @Override
    public Object result(Object stateObj) {
        if (stateObj == null) return null;

        State state = (State) stateObj;

        return state.total > 0
               ? ((double) state.losses / state.total) * 100.0
               : null;
    }
}