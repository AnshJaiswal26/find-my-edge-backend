package com.example.find_my_edge.analytics.ast.reducer.aggregate;

import com.example.find_my_edge.analytics.ast.function.annotation.ArgType;
import com.example.find_my_edge.analytics.ast.function.annotation.FunctionMeta;
import com.example.find_my_edge.analytics.ast.function.enums.ExecutionMode;
import com.example.find_my_edge.analytics.ast.function.enums.FunctionMode;
import com.example.find_my_edge.analytics.ast.function.enums.FunctionType;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@FunctionMeta(
        argTypes = {"number"},
        semanticArgs = {
                @ArgType({"number"})
        },
        returnType = "number",
        semanticReturn = "number",
        signature = "MAX_DRAWDOWN(expr)",
        description = "Maximum drawdown over entire sequence",
        modes = {FunctionMode.AGGREGATE}
)
@Component
public class MaxDrawdownReducer implements Reducer {

    // ---------- STATE ----------
    private static class State {
        double peak = Double.NEGATIVE_INFINITY;
        double equity = 0.0;
        double maxDD = 0.0;
    }

    @Override
    public ExecutionMode getExecutionMode() {
        return ExecutionMode.AST;
    }

    @Override
    public FunctionType getType() {
        return FunctionType.AGGREGATE;
    }

    @Override
    public String getName() {
        return "MAX_DRAWDOWN";
    }


    // ---------- EXECUTION ----------

    @Override
    public Object init() {
        return new State();
    }

    @Override
    public Boolean step(Object stateObj, Object[] args) {
        if (stateObj == null || args == null || args.length < 1) return true;

        State s = (State) stateObj;
        Object valueObj = args[0];

        if (!(valueObj instanceof Number)) return true;

        double value = ((Number) valueObj).doubleValue();

        // accumulate equity (like cumulative PnL)
        s.equity += value;

        // update peak
        if (s.equity > s.peak) {
            s.peak = s.equity;
        }

        // drawdown = peak - current equity
        double dd = s.peak - s.equity;

        // track max drawdown
        if (dd > s.maxDD) {
            s.maxDD = dd;
        }

        return true;
    }

    @Override
    public Object result(Object stateObj) {
        if (stateObj == null) return null;

        State s = (State) stateObj;
        return s.maxDD;
    }
}