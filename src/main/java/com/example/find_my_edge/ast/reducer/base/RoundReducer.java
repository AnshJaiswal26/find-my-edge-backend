package com.example.find_my_edge.ast.reducer.base;

import com.example.find_my_edge.ast.function.FunctionType;
import com.example.find_my_edge.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@Component
public class RoundReducer implements Reducer {

    static class State {
        Double value;
    }

    // ---------- METADATA ----------

    @Override
    public FunctionType getType() {
        return FunctionType.BASE;
    }



    @Override
    public String getName() {
        return "ROUND";
    }

    @Override
    public int getArity() {
        return 2;
    }

    // ---------- EXECUTION ----------

    @Override
    public Object init(int n) {
        return new State();
    }

    @Override
    public boolean step(Object stateObj, Object[] args) {
        State state = (State) stateObj;

        Object value = args[0];
        Object decimals = args.length > 1 ? args[1] : 0;

        if (value == null) return true;

        double v = ((Number) value).doubleValue();
        int d = (decimals != null) ? ((Number) decimals).intValue() : 0;

        double factor = Math.pow(10, d);

        state.value = Math.round(v * factor) / factor;

        return false; // single-step reducer
    }

    @Override
    public Object result(Object stateObj) {
        return ((State) stateObj).value;
    }
}