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
        signature = "LOSS_FACTOR()",
        description = "Gross loss divided by gross profit",
        modes={FunctionMode.AGGREGATE}
)
@Component
public class LossFactorReducer implements Reducer {

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
        return "LOSS_FACTOR";
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

        double lossAbs = Math.abs(state.grossLoss);

        if (state.grossProfit == 0.0) {
            return null; // avoid divide by zero
        }

        return lossAbs / state.grossProfit;
    }
}