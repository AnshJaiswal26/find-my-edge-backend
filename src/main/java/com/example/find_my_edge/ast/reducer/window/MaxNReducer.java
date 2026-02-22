package com.example.find_my_edge.ast.reducer.window;

import com.example.find_my_edge.ast.function.FunctionType;
import com.example.find_my_edge.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@Component
public class MaxNReducer implements Reducer {

    // ---------- STATE ----------
    private static class State {
        final int n;
        int seen;
        Double max;

        State(int n) {
            this.n = n;
            this.seen = 0;
            this.max = null;
        }
    }

    // ---------- METADATA ----------

    @Override
    public FunctionType getType() {
        return FunctionType.WINDOW;
    }

    @Override
    public String getName() {
        return "MAX_N";
    }

    @Override
    public int getArity() {
        return 1; // expr (n comes from engine/config)
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
            double value = ((Number) valueObj).doubleValue();

            state.max = (state.max == null)
                        ? value
                        : Math.max(state.max, value);
        }

        state.seen++;

        // stop when seen >= n
        return state.seen < state.n;
    }

    @Override
    public Object result(Object stateObj) {
        if (stateObj == null) return null;

        State state = (State) stateObj;
        return state.max;
    }
}