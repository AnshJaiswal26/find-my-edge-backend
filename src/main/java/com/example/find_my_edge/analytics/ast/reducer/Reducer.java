package com.example.find_my_edge.analytics.ast.reducer;


import com.example.find_my_edge.analytics.ast.context.EvaluationContext;
import com.example.find_my_edge.analytics.ast.function.ExecutionMode;
import com.example.find_my_edge.analytics.ast.function.FunctionType;
import com.example.find_my_edge.analytics.ast.model.AstNode;

import java.util.function.BiFunction;

public interface Reducer {


    FunctionType getType();

    ExecutionMode getExecutionMode();

    default String getKey() {
        return null;
    }

    String getName();

    // ---------- REDUCER ----------
    default Object init(int n) {
        return null;
    }

    default Object init() {
        return null;
    }

    default Boolean step(Object state, Object[] args) {
        return false;
    }

    default Object result(Object state) {
        return null;
    }

    // ---------- PURE FUNCTION ----------
    default BiFunction<AstNode, EvaluationContext, Object> getExecutor() {
        return null;
    }
}