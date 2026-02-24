package com.example.find_my_edge.analytics.ast.reducer.ratio;

import com.example.find_my_edge.analytics.ast.function.FunctionType;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@Component
public class AvgReducer implements Reducer {

    // ---------- STATE ----------
    private static class State {
        double sum;
        int count;

        State() {
            this.sum = 0.0;
            this.count = 0;
        }
    }

    // ---------- METADATA ----------

    @Override
    public FunctionType getType() {
        return FunctionType.RATIO; // IMPORTANT: not WINDOW
    }

    @Override
    public String getName() {
        return "AVG";
    }

    @Override
    public int getArity() {
        return 1;
    }

    // ---------- EXECUTION ----------

    @Override
    public Object init(int n) {
        // n not used for global aggregate
        return new State();
    }

    @Override
    public boolean step(Object stateObj, Object[] args) {
        if (stateObj == null || args == null || args.length == 0) return true;

        State state = (State) stateObj;
        Object valueObj = args[0];

        if (valueObj != null) {
            double value = ((Number) valueObj).doubleValue();
            state.sum += value;
            state.count++;
        }

        return true; // never stop early (process all rows)
    }

    @Override
    public Object result(Object stateObj) {
        if (stateObj == null) return null;

        State state = (State) stateObj;

        return state.count > 0
               ? state.sum / state.count
               : null;
    }
}