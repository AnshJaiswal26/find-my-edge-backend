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
        signature = "EXPECTANCY_N(expr, n)",
        description = "Trade expectancy (win rate × avg win − loss rate × avg loss)",
        modes = {FunctionMode.WINDOW}
)
@Component
public class ExpectancyNReducer implements Reducer {

    // ---------- STATE ----------
    private static class State {
        final int n;
        int seen;

        int wins;
        int losses;

        double winSum;
        double lossSum; // negative values

        State(int n) {
            this.n = n;
            this.seen = 0;

            this.wins = 0;
            this.losses = 0;

            this.winSum = 0.0;
            this.lossSum = 0.0;
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
        return "EXPECTANCY_N";
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

            if (value > 0) {
                state.wins++;
                state.winSum += value;
            } else if (value < 0) {
                state.losses++;
                state.lossSum += value; // keep negative
            }
        }

        // stop when seen >= n
        return state.seen < state.n;
    }

    @Override
    public Object result(Object stateObj) {
        if (stateObj == null) return null;

        State state = (State) stateObj;

        int totalTrades = state.wins + state.losses;
        if (totalTrades == 0) return 0.0;

        double winRate = (double) state.wins / totalTrades;
        double lossRate = (double) state.losses / totalTrades;

        double avgWin = state.wins > 0 ? state.winSum / state.wins : 0.0;

        double avgLoss = state.losses > 0
                         ? Math.abs(state.lossSum / state.losses)
                         : 0.0;

        return winRate * avgWin - lossRate * avgLoss;
    }
}