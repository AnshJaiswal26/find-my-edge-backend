package com.example.find_my_edge.analytics.ast.reducer.aggregate;

import com.example.find_my_edge.analytics.ast.function.annotation.ArgType;
import com.example.find_my_edge.analytics.ast.function.annotation.FunctionMeta;
import com.example.find_my_edge.analytics.ast.function.enums.ExecutionMode;
import com.example.find_my_edge.analytics.ast.function.enums.FunctionMode;
import com.example.find_my_edge.analytics.ast.function.enums.FunctionType;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@FunctionMeta(
        argTypes = {"any"},
        semanticArgs = {
                @ArgType({"any"})
        },
        returnType = "number",
        semanticReturn = "number",
        signature = "COUNT(expr)",
        description = "Count of non-null values",
        modes = {FunctionMode.AGGREGATE}
)
@Component
public class CountReducer implements Reducer {

    // ---------- STATE ----------
    private static class State {
        int count = 0;
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
        return "COUNT";
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

        if (valueObj != null) {
            s.count++;
        }

        return true;
    }

    @Override
    public Object result(Object stateObj) {
        if (stateObj == null) return null;

        State s = (State) stateObj;
        return s.count;
    }
}