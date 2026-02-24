package com.example.find_my_edge.analytics.ast.reducer.ratio;

import com.example.find_my_edge.analytics.ast.function.FunctionType;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@Component
public class EdgeRatioReducer implements Reducer {

    // ---------- STATE ----------
    private static class State {
        double win;
        double loss;

        State() {
            this.win = 0.0;
            this.loss = 0.0;
        }
    }

    // ---------- METADATA ----------

    @Override
    public FunctionType getType() {
        return FunctionType.RATIO;
    }

    @Override
    public String getName() {
        return "EDGE_RATIO";
    }

    @Override
    public int getArity() {
        return 2; // win, loss
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

        Object winObj = args[0];
        Object lossObj = args[1];

        if (winObj != null) {
            state.win += ((Number) winObj).doubleValue();
        }

        if (lossObj != null) {
            state.loss += ((Number) lossObj).doubleValue();
        }

        return true; // process all rows
    }

    @Override
    public Object result(Object stateObj) {
        if (stateObj == null) return null;

        State state = (State) stateObj;

        return state.loss == 0.0
               ? 0.0
               : (state.win - state.loss) / state.loss;
    }
}