package com.example.find_my_edge.analytics.ast.function;

import com.example.find_my_edge.analytics.ast.context.EvaluationContext;
import com.example.find_my_edge.analytics.ast.evaluator.AstEvaluator;
import com.example.find_my_edge.analytics.ast.exception.AstFunctionException;
import com.example.find_my_edge.analytics.ast.model.AstNode;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import com.example.find_my_edge.analytics.ast.reducer.runner.ReducerRunnerRegistry;
import com.example.find_my_edge.analytics.ast.reducer.runner.ReducerRunnerStrategy;
import com.example.find_my_edge.common.config.AstConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FunctionHandler {

    private final FunctionRegistry registry;
    private final ReducerRunnerRegistry runnerRegistry;

    public Object handle(AstNode ast, EvaluationContext ctx, AstEvaluator evaluator) {

        Reducer fn = registry.get(ast.getFn());

        if (fn == null) {
            throw new AstFunctionException("No function registered for: " + ast.getFn());
        }

        // direct executor (like reducer.exec)
        if (fn.getExecutor() != null) {
            return fn.getExecutor().apply(ast, ctx);
        }

        ReducerRunnerStrategy reducerRunnerStrategy =
                runnerRegistry.get(
                        fn.getType().equals(FunctionType.RATIO)
                        ? FunctionType.GLOBAL.toString()
                        : fn.getType().toString());

        if (reducerRunnerStrategy == null)
            throw new AstFunctionException("No runner found for reducer type: " + fn.getType());

        return reducerRunnerStrategy.run(fn, ast, ctx, evaluator);
    }
}