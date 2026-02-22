package com.example.find_my_edge.ast.reducer.global;

import com.example.find_my_edge.ast.function.FunctionType;
import com.example.find_my_edge.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@Component
public class SumReducer implements Reducer {

    // ---------- STATE ----------
    private static class State {
        double sum = 0.0;
    }

    // ---------- METADATA ----------

    @Override
    public FunctionType getType() {
        return FunctionType.GLOBAL; // ✅ as you wanted
    }

    @Override
    public String getName() {
        return "SUM";
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

        if (valueObj != null) {
            s.sum += ((Number) valueObj).doubleValue();
        }

        return true;
    }

    @Override
    public Object result(Object stateObj) {
        if (stateObj == null) return null;

        State s = (State) stateObj;
        return s.sum;
    }
}