package com.example.find_my_edge.ast.reducer.window;

import com.example.find_my_edge.ast.function.FunctionType;
import com.example.find_my_edge.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@Component
public class SharpeNReducer implements Reducer {

    // ---------- STATE ----------
    private static class State {
        final int n;
        int seen;
        double sum;
        double sumSq;

        State(int n) {
            this.n = n;
            this.seen = 0;
            this.sum = 0.0;
            this.sumSq = 0.0;
        }
    }

    // ---------- METADATA ----------

    @Override
    public FunctionType getType() {
        return FunctionType.WINDOW;
    }

    @Override
    public String getName() {
        return "SHARPE_N";
    }

    @Override
    public int getArity() {
        return 1; // expr (n from engine)
    }

    // ---------- EXECUTION ----------

    @Override
    public Object init(int n) {
        if (n <= 1) return null;
        return new State(n);
    }

    @Override
    public boolean step(Object stateObj, Object[] args) {
        if (stateObj == null || args == null || args.length == 0) return true;

        State state = (State) stateObj;
        Object valueObj = args[0];

        if (valueObj != null) {
            double value = ((Number) valueObj).doubleValue();

            state.seen++;
            state.sum += value;
            state.sumSq += value * value;
        }

        // stop when seen >= n
        return state.seen < state.n;
    }

    @Override
    public Object result(Object stateObj) {
        if (stateObj == null) return null;

        State state = (State) stateObj;

        if (state.seen < 2) return null;

        double mean = state.sum / state.seen;
        double variance = (state.sumSq / state.seen) - (mean * mean);

        if (variance <= 0) return null;

        double std = Math.sqrt(variance);

        return mean / std; // Sharpe ratio (rf = 0)
    }
}