package com.example.find_my_edge.ast.reducer.ratio;

import com.example.find_my_edge.ast.function.FunctionType;
import com.example.find_my_edge.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@Component
public class ProfitFactorReducer implements Reducer {

    // ---------- STATE ----------
    private static class State {
        double grossProfit;
        double grossLoss; // negative values

        State() {
            this.grossProfit = 0.0;
            this.grossLoss = 0.0;
        }
    }

    // ---------- METADATA ----------

    @Override
    public FunctionType getType() {
        return FunctionType.NATIVE_AGG;
    }

    @Override
    public String getName() {
        return "PROFIT_FACTOR";
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
            state.grossProfit += pnl;
        } else if (pnl < 0) {
            state.grossLoss += pnl; // keep negative
        }

        return true; // process all rows
    }

    @Override
    public Object result(Object stateObj) {
        if (stateObj == null) return null;

        State state = (State) stateObj;

        return state.grossLoss == 0.0
               ? null
               : state.grossProfit / Math.abs(state.grossLoss);
    }
}