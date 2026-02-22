package com.example.find_my_edge.ast.reducer;


import com.example.find_my_edge.ast.context.EvaluationContext;
import com.example.find_my_edge.ast.function.FunctionType;
import com.example.find_my_edge.common.dto.AstDTO;

import java.util.function.BiFunction;

public interface Reducer {

    // ---------- METADATA ----------
    FunctionType getType();

    default String getKey() {
        return null;
    }

    String getName();

    int getArity();

    // ---------- OPTIONAL EXECUTOR ----------
    default BiFunction<AstDTO, EvaluationContext, Object> getExecutor() {
        return null;
    }

    // ---------- REDUCER ----------
    Object init(int n);

    boolean step(Object state, Object[] args);

    Object result(Object state);
}