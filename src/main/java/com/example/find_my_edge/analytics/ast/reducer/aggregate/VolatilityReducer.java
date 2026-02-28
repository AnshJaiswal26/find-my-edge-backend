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
        signature = "VOLATILITY(expr)",
        description = "Standard deviation of returns over entire sequence",
        modes = {FunctionMode.AGGREGATE}
)
@Component
public class VolatilityReducer implements Reducer {

    // ---------- STATE ----------
    private static class State {
        int count = 0;
        double mean = 0.0;
        double m2 = 0.0;
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
        return "VOLATILITY";
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

        // Welford's algorithm
        s.count++;
        double delta = value - s.mean;
        s.mean += delta / s.count;
        s.m2 += delta * (value - s.mean);

        return true;
    }

    @Override
    public Object result(Object stateObj) {
        if (stateObj == null) return null;

        State s = (State) stateObj;

        if (s.count < 2) return 0.0;

        // sample standard deviation
        return Math.sqrt(s.m2 / (s.count - 1));
    }
}