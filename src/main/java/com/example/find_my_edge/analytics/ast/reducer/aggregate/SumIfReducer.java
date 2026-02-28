package com.example.find_my_edge.analytics.ast.reducer.aggregate;

import com.example.find_my_edge.analytics.ast.function.annotation.ArgType;
import com.example.find_my_edge.analytics.ast.function.annotation.FunctionMeta;
import com.example.find_my_edge.analytics.ast.function.enums.ExecutionMode;
import com.example.find_my_edge.analytics.ast.function.enums.FunctionMode;
import com.example.find_my_edge.analytics.ast.function.enums.FunctionType;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@FunctionMeta(
        argTypes = {"number", "boolean"},
        semanticArgs = {
                @ArgType({"number", "duration"}),
                @ArgType({"boolean"})
        },
        returnType = "number",
        semanticReturn = "same",
        signature = "SUM_IF(expr, condition)",
        description = "Sum of expr where condition is true",
        modes = {FunctionMode.AGGREGATE}
)
@Component
public class SumIfReducer implements Reducer {

    // ---------- STATE ----------
    private static class State {
        double sum = 0.0;
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
        return "SUM_IF";
    }


    // ---------- EXECUTION ----------

    @Override
    public Object init() {
        return new State();
    }

    @Override
    public Boolean step(Object stateObj, Object[] args) {
        if (stateObj == null || args == null || args.length < 2) return true;

        State s = (State) stateObj;

        Object valueObj = args[0];
        Object condObj = args[1];

        if (valueObj instanceof Number && condObj instanceof Boolean && Boolean.TRUE.equals(valueObj)) {
            s.sum += ((Number) valueObj).doubleValue();
        }

        return true;
    }

    @Override
    public Object result(Object stateObj) {
        if (stateObj == null) return null;

        State s = (State) stateObj;
        return s.sum;
    }
}