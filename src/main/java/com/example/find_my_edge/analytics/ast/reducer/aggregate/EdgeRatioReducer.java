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
        signature = "EDGE_RATIO(win, loss)",
        description = "Net edge ratio between two opposing values",
        modes = {FunctionMode.AGGREGATE}
)
@Component
public class EdgeRatioReducer implements Reducer {

    // ---------- STATE ----------
    private static class State {
        double win;
        double loss;

        State() {
            this.win = 0.0;
            this.loss = 0.0;
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
        return "EDGE_RATIO";
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

        Object winObj = args[0];
        Object lossObj = args[1];

        if (winObj != null) {
            state.win += ((Number) winObj).doubleValue();
        }

        if (lossObj != null) {
            state.loss += ((Number) lossObj).doubleValue();
        }

        return true; // process all rows
    }

    @Override
    public Object result(Object stateObj) {
        if (stateObj == null) return null;

        State state = (State) stateObj;

        return state.loss == 0.0
               ? 0.0
               : (state.win - state.loss) / state.loss;
    }
}