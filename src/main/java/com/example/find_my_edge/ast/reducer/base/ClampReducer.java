package com.example.find_my_edge.ast.reducer.base;

import com.example.find_my_edge.ast.function.FunctionType;
import com.example.find_my_edge.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@Component
public class ClampReducer implements Reducer {

    static class State {
        Double value;
    }

    @Override
    public FunctionType getType() {
        return FunctionType.BASE;
    }



    @Override
    public String getName() {
        return "CLAMP";
    }

    @Override
    public int getArity() {
        return 3;
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
        Object min = args[1];
        Object max = args[2];

        if (value == null || min == null || max == null) return true;

        double v = ((Number) value).doubleValue();
        double minVal = ((Number) min).doubleValue();
        double maxVal = ((Number) max).doubleValue();

        state.value = Math.min(Math.max(v, minVal), maxVal);

        return false; // single-step
    }

    @Override
    public Object result(Object stateObj) {
        return ((State) stateObj).value;
    }
}