package com.example.find_my_edge.analytics.ast.reducer.ratio;

import com.example.find_my_edge.analytics.ast.function.FunctionType;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@Component
public class ExpectancyReducer implements Reducer {

    // ---------- STATE ----------
    private static class State {
        int wins;
        int losses;

        double winSum;
        double lossSum; // stored as positive (abs)

        State() {
            this.wins = 0;
            this.losses = 0;
            this.winSum = 0.0;
            this.lossSum = 0.0;
        }
    }

    // ---------- METADATA ----------

    @Override
    public FunctionType getType() {
        return FunctionType.NATIVE_AGG;
    }

    @Override
    public String getName() {
        return "EXPECTANCY";
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
            state.wins++;
            state.winSum += pnl;
        } else if (pnl < 0) {
            state.losses++;
            state.lossSum += Math.abs(pnl); // store positive
        }

        return true; // process all rows
    }

    @Override
    public Object result(Object stateObj) {
        if (stateObj == null) return null;

        State state = (State) stateObj;

        int total = state.wins + state.losses;
        if (total == 0) return 0.0;

        double winRate = (double) state.wins / total;

        double avgWin = state.wins > 0
                        ? state.winSum / state.wins
                        : 0.0;

        double avgLoss = state.losses > 0
                         ? state.lossSum / state.losses
                         : 0.0;

        return winRate * avgWin - (1 - winRate) * avgLoss;
    }
}