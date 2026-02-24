package com.example.find_my_edge.analytics.ast.reducer.global;

import com.example.find_my_edge.analytics.ast.function.FunctionType;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@Component
public class MaxDrawdownReducer implements Reducer {

    // ---------- STATE ----------
    private static class State {
        double peak = Double.NEGATIVE_INFINITY;
        double equity = 0.0;
        double maxDD = 0.0;
    }

    // ---------- METADATA (minimal required) ----------

    @Override
    public FunctionType getType() {
        return FunctionType.GLOBAL;
    }

    @Override
    public String getName() {
        return "MAX_DRAWDOWN";
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

        // accumulate equity (like cumulative PnL)
        s.equity += value;

        // update peak
        if (s.equity > s.peak) {
            s.peak = s.equity;
        }

        // drawdown = peak - current equity
        double dd = s.peak - s.equity;

        // track max drawdown
        if (dd > s.maxDD) {
            s.maxDD = dd;
        }

        return true;
    }

    @Override
    public Object result(Object stateObj) {
        if (stateObj == null) return null;

        State s = (State) stateObj;
        return s.maxDD;
    }
}