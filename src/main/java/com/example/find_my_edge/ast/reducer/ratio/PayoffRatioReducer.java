package com.example.find_my_edge.ast.reducer.ratio;

import com.example.find_my_edge.ast.function.FunctionType;
import com.example.find_my_edge.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@Component
public class PayoffRatioReducer implements Reducer {

    // ---------- STATE ----------
    private static class State {
        double winSum;
        int winCount;

        double lossSum; // negative
        int lossCount;

        State() {
            this.winSum = 0.0;
            this.winCount = 0;
            this.lossSum = 0.0;
            this.lossCount = 0;
        }
    }

    // ---------- METADATA ----------

    @Override
    public FunctionType getType() {
        return FunctionType.NATIVE_AGG;
    }

    @Override
    public String getName() {
        return "PAYOFF_RATIO";
    }

    @Override
    public int getArity() {
        return 0; // no args
    }

    @Override
    public String getKey() {
        return "pnl"; // 👈 engine injects pnl
    }

    // ---------- EXECUTION ----------

    @Override
    public Object init(int n) {
        return new State(); // n not used
    }

    @Override
    public boolean step(Object stateObj, Object[] args) {
        if (stateObj == null || args == null || args.length == 0) return true;

        State state = (State) stateObj;

        Object pnlObj = args[0];
        if (pnlObj == null) return true;

        double pnl = ((Number) pnlObj).doubleValue();

        if (pnl > 0) {
            state.winSum += pnl;
            state.winCount++;
        } else if (pnl < 0) {
            state.lossSum += pnl; // keep negative
            state.lossCount++;
        }

        return true; // process all rows
    }

    @Override
    public Object result(Object stateObj) {
        if (stateObj == null) return null;

        State state = (State) stateObj;

        if (state.winCount == 0 || state.lossCount == 0) {
            return null;
        }

        double avgWin = state.winSum / state.winCount;
        double avgLoss = Math.abs(state.lossSum / state.lossCount);

        return avgLoss == 0.0
               ? null
               : avgWin / avgLoss;
    }
}