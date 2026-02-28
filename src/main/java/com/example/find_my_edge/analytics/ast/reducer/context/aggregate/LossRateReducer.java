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
        signature = "LOSS_RATE()",
        description = "Losing trades divided by total trades",
        modes = {FunctionMode.AGGREGATE}
)
@Component
public class LossRateReducer implements Reducer {

    // ---------- STATE ----------
    private static class State {
        int total;
        int losses;

        State() {
            this.total = 0;
            this.losses = 0;
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
        return "LOSS_RATE";
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

        state.total++;

        if (pnl < 0) {
            state.losses++;
        }

        return true; // process all rows
    }

    @Override
    public Object result(Object stateObj) {
        if (stateObj == null) return null;

        State state = (State) stateObj;

        return state.total > 0
               ? ((double) state.losses / state.total) * 100.0
               : null;
    }
}