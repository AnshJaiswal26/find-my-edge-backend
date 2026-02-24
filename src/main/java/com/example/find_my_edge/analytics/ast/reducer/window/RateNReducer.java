package com.example.find_my_edge.analytics.ast.reducer.window;

import com.example.find_my_edge.analytics.ast.function.FunctionType;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@Component
public class RateNReducer implements Reducer {

    // ---------- STATE ----------
    private static class State {
        final int n;
        int seen;
        double sum;
        int count;

        State(int n) {
            this.n = n;
            this.seen = 0;
            this.sum = 0.0;
            this.count = 0;
        }
    }

    // ---------- METADATA ----------

    @Override
    public FunctionType getType() {
        return FunctionType.WINDOW;
    }

    @Override
    public String getName() {
        return "RATE_N";
    }

    @Override
    public int getArity() {
        return 1; // boolean condition (n comes from engine/config)
    }

    // ---------- EXECUTION ----------

    @Override
    public Object init(int n) {
        if (n <= 0) return null;
        return new State(n);
    }

    @Override
    public boolean step(Object stateObj, Object[] args) {
        if (stateObj == null || args == null || args.length == 0) return true;

        State state = (State) stateObj;
        Object valueObj = args[0];

        if (valueObj != null) {
            boolean condition = (Boolean) valueObj;

            double value = condition ? 1.0 : 0.0;

            state.sum += value;
            state.count++;
        }

        state.seen++;

        // stop when seen >= n
        return state.seen < state.n;
    }

    @Override
    public Object result(Object stateObj) {
        if (stateObj == null) return null;

        State state = (State) stateObj;

        // rate between 0 → 1
        return state.count > 0 ? state.sum / state.count : null;
    }
}