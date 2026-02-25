package com.example.find_my_edge.analytics.ast.reducer.runner;

import com.example.find_my_edge.analytics.ast.context.EvaluationContext;
import com.example.find_my_edge.analytics.ast.evaluator.AstEvaluator;
import com.example.find_my_edge.analytics.ast.model.AstNode;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;

public interface ReducerRunnerStrategy {
    Object run(Reducer reducer, AstNode fn, EvaluationContext ctx, AstEvaluator evaluator);
}
