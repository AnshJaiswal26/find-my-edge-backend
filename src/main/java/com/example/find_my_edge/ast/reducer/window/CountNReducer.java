package com.example.find_my_edge.ast.reducer.window;

import com.example.find_my_edge.ast.function.FunctionType;
import com.example.find_my_edge.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@Component
public class CountNReducer implements Reducer {

    // ---------- STATE ----------
    private static class State {
        int n;
        int seen;
        int count;

        State(int n) {
            this.n = n;
            this.seen = 0;
            this.count = 0;
        }
    }

    // ---------- METADATA ----------

    @Override
    public FunctionType getType() {
        return FunctionType.WINDOW;
    }

    @Override
    public String getName() {
        return "COUNT_N";
    }

    @Override
    public int getArity() {
        return 1; // expr only (n handled by engine)
    }

    // ---------- EXECUTION ----------

    @Override
    public Object init(int n) {
        if (n <= 0) return null;
        return new State(n);
    }

    @Override
    public boolean step(Object stateObj, Object[] args) {
        if (stateObj == null || args == null || args.length == 0) return true;

        State state = (State) stateObj;
        Object value = args[0];

        if (value != null) {
            state.count++;
        }

        state.seen++;

        return state.seen < state.n;
    }

    @Override
    public Object result(Object stateObj) {
        if (stateObj == null) return null;

        State state = (State) stateObj;
        return state.count;
    }
}