package com.example.find_my_edge.analytics.ast.reducer;


import com.example.find_my_edge.analytics.ast.context.EvaluationContext;
import com.example.find_my_edge.analytics.ast.function.FunctionType;
import com.example.find_my_edge.analytics.ast.model.AstNode;
import com.example.find_my_edge.common.config.AstConfig;

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
    default BiFunction<AstNode, EvaluationContext, Object> getExecutor() {
        return null;
    }

    // ---------- REDUCER ----------
    Object init(int n);

    boolean step(Object state, Object[] args);

    Object result(Object state);
}