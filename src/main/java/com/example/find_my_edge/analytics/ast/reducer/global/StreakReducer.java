package com.example.find_my_edge.analytics.ast.reducer.global;

import com.example.find_my_edge.analytics.ast.function.FunctionType;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@Component
public class StreakReducer implements Reducer {

    // ---------- STATE ----------
    private static class State {
        int max = 0;
        int current = 0;
    }

    // ---------- METADATA (minimal required) ----------

    @Override
    public FunctionType getType() {
        return FunctionType.GLOBAL;
    }

    @Override
    public String getName() {
        return "STREAK";
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
        Object condObj = args[0];

        if (condObj instanceof Boolean && (Boolean) condObj) {
            s.current++;
            if (s.current > s.max) {
                s.max = s.current;
            }
        } else {
            s.current = 0;
        }

        return true;
    }

    @Override
    public Object result(Object stateObj) {
        if (stateObj == null) return null;

        State s = (State) stateObj;
        return s.max;
    }
}