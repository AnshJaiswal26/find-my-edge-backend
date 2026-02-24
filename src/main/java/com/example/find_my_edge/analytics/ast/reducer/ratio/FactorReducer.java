package com.example.find_my_edge.analytics.ast.reducer.ratio;

import com.example.find_my_edge.analytics.ast.function.FunctionType;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@Component
public class FactorReducer implements Reducer {

    // ---------- STATE ----------
    private static class State {
        double a;
        double b;

        State() {
            this.a = 0.0;
            this.b = 0.0;
        }
    }

    // ---------- METADATA ----------

    @Override
    public FunctionType getType() {
        return FunctionType.RATIO;
    }

    @Override
    public String getName() {
        return "FACTOR";
    }

    @Override
    public int getArity() {
        return 2; // a, b
    }

    // ---------- EXECUTION ----------

    @Override
    public Object init(int n) {
        return new State(); // n not used
    }

    @Override
    public boolean step(Object stateObj, Object[] args) {
        if (stateObj == null || args == null || args.length < 2) return true;

        State state = (State) stateObj;

        Object aObj = args[0];
        Object bObj = args[1];

        if (aObj != null) {
            state.a += ((Number) aObj).doubleValue();
        }

        if (bObj != null) {
            state.b += ((Number) bObj).doubleValue();
        }

        return true; // process all rows
    }

    @Override
    public Object result(Object stateObj) {
        if (stateObj == null) return null;

        State state = (State) stateObj;

        return state.b == 0.0
               ? 0.0
               : state.a / state.b;
    }
}