package com.example.find_my_edge.analytics.ast.reducer.aggregate;

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
        signature = "FACTOR(a, b)",
        description = "Magnitude comparison between two values (a / b)",
        modes = {FunctionMode.AGGREGATE}
)
@Component
public class FactorReducer implements Reducer {

    // ---------- STATE ----------
    private static class State {
        double a;
        double b;

        State() {
            this.a = 0.0;
            this.b = 0.0;
        }
    }



    @Override
    public FunctionType getType() {
        return FunctionType.AGGREGATE;
    }

    @Override
    public ExecutionMode getExecutionMode() {
        return ExecutionMode.AST;
    }

    @Override
    public String getName() {
        return "FACTOR";
    }



    // ---------- EXECUTION ----------

    @Override
    public Object init() {
        return new State(); // n not used
    }

    @Override
    public Boolean step(Object stateObj, Object[] args) {
        if (stateObj == null || args == null || args.length < 2) return true;

        State state = (State) stateObj;

        Object aObj = args[0];
        Object bObj = args[1];

        if (aObj != null) {
            state.a += ((Number) aObj).doubleValue();
        }

        if (bObj != null) {
            state.b += ((Number) bObj).doubleValue();
        }

        return true; // process all rows
    }

    @Override
    public Object result(Object stateObj) {
        if (stateObj == null) return null;

        State state = (State) stateObj;

        return state.b == 0.0
               ? 0.0
               : state.a / state.b;
    }
}