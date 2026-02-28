package com.example.find_my_edge.analytics.ast.reducer.aggregate;

import com.example.find_my_edge.analytics.ast.function.annotation.ArgType;
import com.example.find_my_edge.analytics.ast.function.enums.ExecutionMode;
import com.example.find_my_edge.analytics.ast.function.annotation.FunctionMeta;
import com.example.find_my_edge.analytics.ast.function.enums.FunctionMode;
import com.example.find_my_edge.analytics.ast.function.enums.FunctionType;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@FunctionMeta(
        argTypes = {"number"},
        semanticArgs = {
                @ArgType({"number", "duration"})
        },
        returnType = "number",
        semanticReturn = "same",
        signature = "AVG(expr)",
        description = "Average (mean) of values",
        modes={FunctionMode.AGGREGATE}
)
@Component
public class AvgReducer implements Reducer {

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
        return FunctionType.AGGREGATE; // IMPORTANT: not WINDOW
    }

    @Override
    public String getName() {
        return "AVG";
    }



    // ---------- EXECUTION ----------

    @Override
    public Object init() {
        // n not used for global aggregate
        return new State();
    }

    @Override
    public Boolean step(Object stateObj, Object[] args) {
        if (stateObj == null || args == null || args.length == 0) return true;

        State state = (State) stateObj;
        Object valueObj = args[0];

        if (valueObj != null) {
            double value = ((Number) valueObj).doubleValue();
            state.sum += value;
            state.count++;
        }

        return true; // never stop early (process all rows)
    }

    @Override
    public Object result(Object stateObj) {
        if (stateObj == null) return null;

        State state = (State) stateObj;

        return state.count > 0
               ? state.sum / state.count
               : null;
    }
}