package com.example.find_my_edge.analytics.ast.reducer.aggregate;

import com.example.find_my_edge.analytics.ast.function.annotation.ArgType;
import com.example.find_my_edge.analytics.ast.function.annotation.FunctionMeta;
import com.example.find_my_edge.analytics.ast.function.enums.ExecutionMode;
import com.example.find_my_edge.analytics.ast.function.enums.FunctionMode;
import com.example.find_my_edge.analytics.ast.function.enums.FunctionType;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@FunctionMeta(
        argTypes = {"boolean"},
        semanticArgs = {
                @ArgType({"boolean"})
        },
        returnType = "number",
        semanticReturn = "number",
        signature = "STREAK(condition)",
        description = "Longest consecutive TRUE streak over all rows",
        modes = {FunctionMode.AGGREGATE}
)
@Component
public class StreakReducer implements Reducer {

    // ---------- STATE ----------
    private static class State {
        int max = 0;
        int current = 0;
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
        return "STREAK";
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
        Object condObj = args[0];

        if (condObj instanceof Boolean && (Boolean) condObj) {
            s.current++;
            if (s.current > s.max) {
                s.max = s.current;
            }
        } else {
            s.current = 0;
        }

        return true;
    }

    @Override
    public Object result(Object stateObj) {
        if (stateObj == null) return null;

        State s = (State) stateObj;
        return s.max;
    }
}