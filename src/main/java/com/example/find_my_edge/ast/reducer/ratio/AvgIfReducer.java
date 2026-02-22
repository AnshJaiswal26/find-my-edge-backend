package com.example.find_my_edge.ast.reducer.ratio;

import com.example.find_my_edge.ast.function.FunctionType;
import com.example.find_my_edge.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@Component
public class AvgIfReducer implements Reducer {

    // ---------- STATE ----------
    private static class State {
        double sum;
        int count;

        State() {
            this.sum = 0.0;
            this.count = 0;
        }
    }

    // ---------- METADATA ----------

    @Override
    public FunctionType getType() {
        // For now
        return FunctionType.RATIO;

    }

    @Override
    public String getName() {
        return "AVG_IF";
    }

    @Override
    public int getArity() {
        return 2; // expr, condition
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

        Object valueObj = args[0];
        Object condObj = args[1];

        if (valueObj != null && condObj != null && (Boolean) condObj) {
            double value = ((Number) valueObj).doubleValue();
            state.sum += value;
            state.count++;
        }

        return true; // process all rows
    }

    @Override
    public Object result(Object stateObj) {
        if (stateObj == null) return null;

        State state = (State) stateObj;

        return state.count == 0
               ? 0.0   // matches your JS
               : state.sum / state.count;
    }
}