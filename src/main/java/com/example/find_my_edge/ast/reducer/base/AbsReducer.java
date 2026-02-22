package com.example.find_my_edge.ast.reducer.base;

import com.example.find_my_edge.ast.function.FunctionType;
import com.example.find_my_edge.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@Component
public class AbsReducer implements Reducer {

    static class State {
        Double value;
    }

    @Override
    public FunctionType getType() {
        return FunctionType.BASE;
    }


    @Override
    public String getName() {
        return "ABS";
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
        State state = (State) stateObj;

        Object val = args[0];
        if (val == null) return true;

        state.value = Math.abs(((Number) val).doubleValue());

        return false; // single-step function
    }

    @Override
    public Object result(Object stateObj) {
        return ((State) stateObj).value;
    }
}