package com.example.find_my_edge.analytics.ast.reducer.aggregate;

import com.example.find_my_edge.analytics.ast.function.ArgType;
import com.example.find_my_edge.analytics.ast.function.ExecutionMode;
import com.example.find_my_edge.analytics.ast.function.FunctionMeta;
import com.example.find_my_edge.analytics.ast.function.FunctionType;
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
        signature = "AVG_IF(expr, condition)",
        description = "Average of expr where condition is true"
)
@Component
public class AvgIfReducer implements Reducer {

    // ---------- STATE ----------
    private static class State {
        double sum;
        int count;

        State() {
            this.sum = 0.0;
            this.count = 0;
        }
    }

    @Override
    public ExecutionMode getExecutionMode() {
        return ExecutionMode.AST;
    }

    @Override
    public FunctionType getType() {
        // For now
        return FunctionType.AGGREGATE;
    }

    @Override
    public String getName() {
        return "AVG_IF";
    }

    // ---------- EXECUTION ----------

    @Override
    public Object init(int n) {
        return new State(); // n not used
    }

    @Override
    public Boolean step(Object stateObj, Object[] args) {
        if (stateObj == null || args == null || args.length < 2) return true;

        State state = (State) stateObj;

        Object valueObj = args[0];
        Object condObj = args[1];

        if (valueObj != null && condObj != null && (Boolean) condObj) {
            double value = ((Number) valueObj).doubleValue();
            state.sum += value;
            state.count++;
        }

        return true; // process all rows
    }

    @Override
    public Object result(Object stateObj) {
        if (stateObj == null) return null;

        State state = (State) stateObj;

        return state.count == 0
               ? 0.0   // matches your JS
               : state.sum / state.count;
    }
}