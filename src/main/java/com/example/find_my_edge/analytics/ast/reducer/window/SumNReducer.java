package com.example.find_my_edge.analytics.ast.reducer.window;

import com.example.find_my_edge.analytics.ast.function.annotation.ArgType;
import com.example.find_my_edge.analytics.ast.function.annotation.FunctionMeta;
import com.example.find_my_edge.analytics.ast.function.enums.ExecutionMode;
import com.example.find_my_edge.analytics.ast.function.enums.FunctionMode;
import com.example.find_my_edge.analytics.ast.function.enums.FunctionType;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@FunctionMeta(
        argTypes = {"number", "number"},
        semanticArgs = {
                @ArgType({"number", "duration"}),
                @ArgType({"number"})
        },
        returnType = "number",
        semanticReturn = "same",
        signature = "SUM_N(expr, n)",
        description = "Rolling sum over N rows",
        modes = {FunctionMode.WINDOW}
)
@Component
public class SumNReducer implements Reducer {

    // ---------- STATE ----------
    private static class State {
        int n;
        int seen;
        double sum;

        State(int n) {
            this.n = n;
            this.seen = 0;
            this.sum = 0.0;
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
        return "SUM_N";
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
            state.sum += value;
        }

        state.seen++;

        // stop when seen >= n
        return state.seen < state.n;
    }

    @Override
    public Object result(Object stateObj) {
        if (stateObj == null) return null;

        State state = (State) stateObj;
        return state.sum;
    }
}