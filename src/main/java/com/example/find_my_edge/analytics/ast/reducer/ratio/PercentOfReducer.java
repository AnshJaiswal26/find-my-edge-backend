package com.example.find_my_edge.analytics.ast.reducer.ratio;

import com.example.find_my_edge.analytics.ast.function.FunctionType;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@Component
public class PercentOfReducer implements Reducer {

    // ---------- STATE ----------
    private static class State {
        double part;
        double total;

        State() {
            this.part = 0.0;
            this.total = 0.0;
        }
    }

    // ---------- METADATA ----------

    @Override
    public FunctionType getType() {
        return FunctionType.RATIO;
    }

    @Override
    public String getName() {
        return "PERCENT_OF";
    }

    @Override
    public int getArity() {
        return 2; // part, total
    }

    // ---------- EXECUTION ----------

    @Override
    public Object init(int n) {
        return new State(); // n not used
    }

    @Override
    public boolean step(Object stateObj, Object[] args) {
        if (stateObj == null || args == null || args.length < 2) return true;

        State state = (State) stateObj;

        Object partObj = args[0];
        Object totalObj = args[1];

        if (partObj != null) {
            state.part += ((Number) partObj).doubleValue();
        }

        if (totalObj != null) {
            state.total += ((Number) totalObj).doubleValue();
        }

        return true; // process all rows
    }

    @Override
    public Object result(Object stateObj) {
        if (stateObj == null) return null;

        State state = (State) stateObj;

        return state.total == 0.0
               ? 0.0
               : (state.part / state.total) * 100.0;
    }
}