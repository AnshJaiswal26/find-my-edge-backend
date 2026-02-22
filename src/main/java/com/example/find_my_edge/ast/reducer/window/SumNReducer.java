package com.example.find_my_edge.ast.reducer.window;

import com.example.find_my_edge.ast.function.FunctionType;
import com.example.find_my_edge.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@Component
public class SumNReducer implements Reducer {

    // ---------- STATE ----------
    private static class State {
        int n;
        int seen;
        double sum;

        State(int n) {
            this.n = n;
            this.seen = 0;
            this.sum = 0.0;
        }
    }

    // ---------- METADATA ----------

    @Override
    public FunctionType getType() {
        return FunctionType.WINDOW;
    }

    @Override
    public String getName() {
        return "SUM_N";
    }

    @Override
    public int getArity() {
        return 1; // value only (n handled by engine)
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
            state.sum += value;
        }

        state.seen++;

        // stop when seen >= n
        return state.seen < state.n;
    }

    @Override
    public Object result(Object stateObj) {
        if (stateObj == null) return null;

        State state = (State) stateObj;
        return state.sum;
    }
}