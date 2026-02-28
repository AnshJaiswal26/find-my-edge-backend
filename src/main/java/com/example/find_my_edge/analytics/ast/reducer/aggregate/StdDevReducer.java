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
        signature = "STDDEV(expr)",
        description = "Standard deviation of values",
        modes = {FunctionMode.AGGREGATE}
)
@Component
public class StdDevReducer implements Reducer {

    // ---------- STATE ----------
    private static class State {
        int n = 0;
        double mean = 0.0;
        double m2 = 0.0; // sum of squares of differences
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
        return "STDDEV";
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

        double x = ((Number) valueObj).doubleValue();

        // Welford's algorithm
        s.n++;
        double delta = x - s.mean;
        s.mean += delta / s.n;
        s.m2 += delta * (x - s.mean);

        return true;
    }

    @Override
    public Object result(Object stateObj) {
        if (stateObj == null) return null;

        State s = (State) stateObj;

        return s.n > 1 ? Math.sqrt(s.m2 / s.n) : 0.0;
    }
}