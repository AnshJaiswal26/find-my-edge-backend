package com.example.find_my_edge.analytics.ast.reducer.global;

import com.example.find_my_edge.analytics.ast.function.FunctionType;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@Component
public class VarianceReducer implements Reducer {

    // ---------- STATE ----------
    private static class State {
        int n = 0;
        double mean = 0.0;
        double m2 = 0.0; // sum of squared differences
    }

    // ---------- METADATA (minimal required) ----------

    @Override
    public FunctionType getType() {
        return FunctionType.GLOBAL;
    }

    @Override
    public String getName() {
        return "VARIANCE";
    }

    @Override
    public int getArity() {
        return 1;
    }

    // ---------- EXECUTION ----------

    @Override
    public Object init(int n) {
        return new State();
    }

    @Override
    public boolean step(Object stateObj, Object[] args) {
        if (stateObj == null || args == null || args.length < 1) return true;

        State s = (State) stateObj;
        Object valueObj = args[0];

        if (!(valueObj instanceof Number)) return true;

        double x = ((Number) valueObj).doubleValue();

        // Welford's algorithm
        s.n++;
        double delta = x - s.mean;
        s.mean += delta / s.n;
        s.m2 += delta * (x - s.mean);

        return true;
    }

    @Override
    public Object result(Object stateObj) {
        if (stateObj == null) return null;

        State s = (State) stateObj;

        return s.n > 1 ? (s.m2 / s.n) : 0.0;
    }
}