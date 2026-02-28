package com.example.find_my_edge.analytics.ast.reducer.window;

import com.example.find_my_edge.analytics.ast.function.annotation.ArgType;
import com.example.find_my_edge.analytics.ast.function.enums.ExecutionMode;
import com.example.find_my_edge.analytics.ast.function.annotation.FunctionMeta;
import com.example.find_my_edge.analytics.ast.function.enums.FunctionMode;
import com.example.find_my_edge.analytics.ast.function.enums.FunctionType;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@FunctionMeta(
        argTypes = {"number", "number"},
        semanticArgs = {
                @ArgType({"number"}),
                @ArgType({"number"})
        },
        returnType = "number",
        semanticReturn = "number",
        signature = "MAX_DRAWDOWN_N(expr, n)",
        description = "Maximum drawdown over last N rows",
        modes = {FunctionMode.WINDOW}
)
@Component
public class MaxDrawdownNReducer implements Reducer {

    // ---------- STATE ----------
    private static class State {
        final int n;
        int seen;
        double peak;
        double maxDrawdown;

        State(int n) {
            this.n = n;
            this.seen = 0;
            this.peak = Double.NEGATIVE_INFINITY;
            this.maxDrawdown = 0.0;
        }
    }

    @Override
    public ExecutionMode getExecutionMode() {
        return ExecutionMode.AST;
    }

    @Override
    public FunctionType getType() {
        return FunctionType.WINDOW;
    }

    @Override
    public String getName() {
        return "MAX_DRAWDOWN_N";
    }

 // ---------- EXECUTION ----------

    @Override
    public Object init(int n) {
        if (n <= 0) return null;
        return new State(n);
    }

    @Override
    public Boolean step(Object stateObj, Object[] args) {
        if (stateObj == null || args == null || args.length == 0) return true;

        State state = (State) stateObj;
        Object valueObj = args[0];

        if (valueObj != null) {
            double value = ((Number) valueObj).doubleValue();

            state.seen++;

            // update peak
            if (value > state.peak) {
                state.peak = value;
            }

            // compute drawdown (negative or zero)
            double drawdown = value - state.peak;

            // track worst drawdown
            if (drawdown < state.maxDrawdown) {
                state.maxDrawdown = drawdown;
            }
        }

        // stop when seen >= n
        return state.seen < state.n;
    }

    @Override
    public Object result(Object stateObj) {
        if (stateObj == null) return null;

        State state = (State) stateObj;
        return state.maxDrawdown; // negative or 0
    }
}