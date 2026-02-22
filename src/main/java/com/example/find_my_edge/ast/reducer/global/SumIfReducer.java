package com.example.find_my_edge.ast.reducer.global;

import com.example.find_my_edge.ast.function.FunctionType;
import com.example.find_my_edge.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@Component
public class SumIfReducer implements Reducer {

    // ---------- STATE ----------
    private static class State {
        double sum = 0.0;
    }

    // ---------- METADATA (minimal required) ----------

    @Override
    public FunctionType getType() {
        return FunctionType.GLOBAL;
    }

    @Override
    public String getName() {
        return "SUM_IF";
    }

    @Override
    public int getArity() {
        return 2; // expr, condition
    }

    // ---------- EXECUTION ----------

    @Override
    public Object init(int n) {
        return new State();
    }

    @Override
    public boolean step(Object stateObj, Object[] args) {
        if (stateObj == null || args == null || args.length < 2) return true;

        State s = (State) stateObj;

        Object valueObj = args[0];
        Object condObj = args[1];

        if (valueObj instanceof Number && condObj instanceof Boolean && (Boolean) condObj) {
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