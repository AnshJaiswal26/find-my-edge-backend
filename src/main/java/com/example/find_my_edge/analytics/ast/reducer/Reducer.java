package com.example.find_my_edge.analytics.ast.reducer;

import com.example.find_my_edge.analytics.ast.context.EvaluationContext;
import com.example.find_my_edge.analytics.ast.evaluator.AstEvaluator;
import com.example.find_my_edge.analytics.ast.function.enums.ExecutionMode;
import com.example.find_my_edge.analytics.ast.function.enums.FunctionType;
import com.example.find_my_edge.analytics.ast.model.AstNode;

public interface Reducer {

    FunctionType getType();

    ExecutionMode getExecutionMode();

    default String getField() {
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

    // ---------- PURE ----------
    default Object execute(AstNode fn, EvaluationContext ctx, AstEvaluator evaluator) {
        throw new UnsupportedOperationException("Not a PURE function");
    }
}