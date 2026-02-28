package com.example.find_my_edge.analytics.ast.reducer.context.aggregate;

import com.example.find_my_edge.analytics.ast.function.enums.ExecutionMode;
import com.example.find_my_edge.analytics.ast.function.annotation.FunctionMeta;
import com.example.find_my_edge.analytics.ast.function.enums.FunctionMode;
import com.example.find_my_edge.analytics.ast.function.enums.FunctionType;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@FunctionMeta(
        argTypes = {},
        semanticArgs = {},
        returnType = "number",
        semanticReturn = "number",
        signature = "PAYOFF_RATIO()",
        description = "Average win divided by average loss",
        modes={FunctionMode.AGGREGATE}
)
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
        return "PAYOFF_RATIO";
    }


    @Override
    public String getField() {
        return "pnl"; // 👈 engine injects pnl
    }

    // ---------- EXECUTION ----------

    @Override
    public Object init() {
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