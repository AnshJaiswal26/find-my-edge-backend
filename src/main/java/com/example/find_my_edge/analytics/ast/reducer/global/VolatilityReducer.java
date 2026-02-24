package com.example.find_my_edge.analytics.ast.reducer.global;

import com.example.find_my_edge.analytics.ast.function.FunctionType;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@Component
public class VolatilityReducer implements Reducer {

    // ---------- STATE ----------
    private static class State {
        int count = 0;
        double mean = 0.0;
        double m2 = 0.0;
    }

    // ---------- METADATA (minimal required) ----------

    @Override
    public FunctionType getType() {
        return FunctionType.GLOBAL;
    }

    @Override
    public String getName() {
        return "VOLATILITY";
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

        double value = ((Number) valueObj).doubleValue();

        // Welford's algorithm
        s.count++;
        double delta = value - s.mean;
        s.mean += delta / s.count;
        s.m2 += delta * (value - s.mean);

        return true;
    }

    @Override
    public Object result(Object stateObj) {
        if (stateObj == null) return null;

        State s = (State) stateObj;

        if (s.count < 2) return 0.0;

        // sample standard deviation
        return Math.sqrt(s.m2 / (s.count - 1));
    }
}