package com.example.find_my_edge.analytics.ast.reducer.window;

import com.example.find_my_edge.analytics.ast.function.ArgType;
import com.example.find_my_edge.analytics.ast.function.ExecutionMode;
import com.example.find_my_edge.analytics.ast.function.FunctionMeta;
import com.example.find_my_edge.analytics.ast.function.FunctionType;
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
        signature = "VOLATILITY_N(expr, n)",
        description = "Standard deviation of returns over last N rows"
)
@Component
public class VolatilityNReducer implements Reducer {

    // ---------- STATE ----------
    private static class State {
        final int n;
        int seen;

        Double prev;

        // Welford
        int count;
        double mean;
        double m2;

        State(int n) {
            this.n = n;
            this.seen = 0;
            this.prev = null;

            this.count = 0;
            this.mean = 0.0;
            this.m2 = 0.0;
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
        return "VOLATILITY_N";
    }


    // ---------- EXECUTION ----------

    @Override
    public Object init(int n) {
        if (n <= 1) return null;
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

            if (state.prev != null) {
                double ret = value - state.prev;

                // Welford update on returns
                state.count++;

                double delta = ret - state.mean;
                state.mean += delta / state.count;
                state.m2 += delta * (ret - state.mean);
            }

            state.prev = value;
        }

        // stop when seen >= n
        return state.seen < state.n;
    }

    @Override
    public Object result(Object stateObj) {
        if (stateObj == null) return null;

        State state = (State) stateObj;

        if (state.count == 0) return 0.0;

        double variance = state.m2 / state.count;
        return Math.sqrt(variance);
    }
}