package com.example.find_my_edge.analytics.ast.reducer.global;

import com.example.find_my_edge.analytics.ast.function.FunctionType;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@Component
public class MaxReducer implements Reducer {

    // ---------- STATE ----------
    private static class State {
        double max = Double.NEGATIVE_INFINITY;
    }

    // ---------- METADATA (minimal required) ----------

    @Override
    public FunctionType getType() {
        return FunctionType.GLOBAL;
    }

    @Override
    public String getName() {
        return "MAX";
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

        if (valueObj instanceof Number) {
            double v = ((Number) valueObj).doubleValue();
            if (v > s.max) {
                s.max = v;
            }
        }

        return true;
    }

    @Override
    public Object result(Object stateObj) {
        if (stateObj == null) return null;

        State s = (State) stateObj;

        return s.max == Double.NEGATIVE_INFINITY ? null : s.max;
    }
}