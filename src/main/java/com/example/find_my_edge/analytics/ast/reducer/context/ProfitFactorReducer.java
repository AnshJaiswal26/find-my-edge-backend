package com.example.find_my_edge.analytics.ast.reducer.context;

import com.example.find_my_edge.analytics.ast.function.ExecutionMode;
import com.example.find_my_edge.analytics.ast.function.FunctionMeta;
import com.example.find_my_edge.analytics.ast.function.FunctionType;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@FunctionMeta(
        argTypes = {},
        semanticArgs = {},
        returnType = "number",
        semanticReturn = "number",
        signature = "PROFIT_FACTOR()",
        description = "Gross profit divided by gross loss"
)
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

    @Override
    public FunctionType getType() {
        return FunctionType.AGGREGATE;
    }

    @Override
    public ExecutionMode getExecutionMode() {
        return ExecutionMode.NATIVE;
    }

    @Override
    public String getName() {
        return "PROFIT_FACTOR";
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
    public Boolean step(Object stateObj, Object[] args) {
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