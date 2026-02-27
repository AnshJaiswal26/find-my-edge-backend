package com.example.find_my_edge.analytics.ast.function;

import com.example.find_my_edge.analytics.ast.context.EvaluationContext;
import com.example.find_my_edge.analytics.ast.evaluator.AstEvaluator;
import com.example.find_my_edge.analytics.ast.exception.AstFunctionException;
import com.example.find_my_edge.analytics.ast.model.AstNode;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import com.example.find_my_edge.analytics.ast.reducer.runner.ReducerRunnerRegistry;
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

        if (fn.getType() == FunctionType.PURE) {
            if (fn.getExecutor() == null) {
                throw new AstFunctionException("No Executor found for: " + ast.getFn());
            }
            return fn.getExecutor().apply(ast, ctx);
        }

        String runnerKey;

        if (fn.getType() == FunctionType.AGGREGATE) {
            runnerKey = fn.getExecutionMode() == ExecutionMode.NATIVE
                        ? "NATIVE_AGG"
                        : "AGGREGATE";
        } else if (fn.getType() == FunctionType.WINDOW) {
            runnerKey = fn.getExecutionMode() == ExecutionMode.NATIVE
                        ? "NATIVE_WINDOW"
                        : "WINDOW";
        } else {
            throw new AstFunctionException("Unsupported type: " + fn.getType());
        }

        var runner = runnerRegistry.get(runnerKey);

        if (runner == null) {
            throw new AstFunctionException("No runner found for: " + runnerKey);
        }

        return runner.run(fn, ast, ctx, evaluator);
    }
}