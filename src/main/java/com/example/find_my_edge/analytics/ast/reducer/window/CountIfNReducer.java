package com.example.find_my_edge.analytics.ast.reducer.window;

import com.example.find_my_edge.analytics.ast.function.FunctionType;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

@Component
public class CountIfNReducer implements Reducer {

    private final SumNReducer sumN = new SumNReducer();

    // ---------- METADATA ----------

    @Override
    public FunctionType getType() {
        return FunctionType.WINDOW;
    }

    @Override
    public String getName() {
        return "COUNT_IF_N";
    }

    @Override
    public int getArity() {
        return 1; // condition only (n handled by engine)
    }

    // ---------- EXECUTION (DELEGATION) ----------

    @Override
    public Object init(int n) {
        return sumN.init(n);
    }

    @Override
    public boolean step(Object state, Object[] args) {
        if (args == null || args.length == 0) return true;

        Object conditionObj = args[0];

        // convert boolean → 1 or 0
        double value = (conditionObj != null && (Boolean) conditionObj) ? 1.0 : 0.0;

        return sumN.step(state, new Object[]{value});
    }

    @Override
    public Object result(Object state) {
        return sumN.result(state);
    }
}