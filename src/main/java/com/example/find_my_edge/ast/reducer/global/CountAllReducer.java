package com.example.find_my_edge.ast.reducer.global;

import com.example.find_my_edge.ast.function.FunctionType;
import com.example.find_my_edge.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@Component
public class CountAllReducer implements Reducer {

    // ---------- STATE ----------
    private static class State {
        int count = 0;
    }

    // ---------- METADATA (minimal required) ----------

    @Override
    public FunctionType getType() {
        return FunctionType.GLOBAL;
    }

    @Override
    public String getName() {
        return "COUNT_ALL";
    }

    @Override
    public int getArity() {
        return 0; // ✅ no arguments
    }

    // ---------- EXECUTION ----------

    @Override
    public Object init(int n) {
        return new State();
    }

    @Override
    public boolean step(Object stateObj, Object[] args) {
        if (stateObj == null) return true;

        State s = (State) stateObj;

        // JS: step: (s) => { s.count++; }
        s.count++;

        return true;
    }

    @Override
    public Object result(Object stateObj) {
        if (stateObj == null) return null;

        State s = (State) stateObj;
        return s.count;
    }
}