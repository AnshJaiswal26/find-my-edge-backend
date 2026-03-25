package com.example.find_my_edge.analytics.ast.function;

import com.example.find_my_edge.analytics.ast.context.EvaluationContext;
import com.example.find_my_edge.analytics.ast.evaluator.AstEvaluator;
import com.example.find_my_edge.analytics.ast.exception.AstFunctionException;
import com.example.find_my_edge.analytics.ast.function.enums.ExecutionMode;
import com.example.find_my_edge.analytics.ast.function.enums.FunctionType;
import com.example.find_my_edge.analytics.ast.function.enums.WindowStrategy;
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

        FunctionDefinition fnDef = registry.get(ast.getFn());

        Reducer reducer = fnDef.getReducer();
        FunctionMeta fnDefMeta = fnDef.getMeta();

        if (reducer == null) {
            throw new AstFunctionException("No function registered for: " + ast.getFn());
        }

        if (fnDefMeta.getType() == FunctionType.PURE ||
            (fnDefMeta.getType() == FunctionType.WINDOW &&
             fnDefMeta.getStrategy() == WindowStrategy.CUMULATIVE)) {
            return reducer.execute(ast, ctx, evaluator);
        }

        String runnerKey;

        if (fnDefMeta.getType() == FunctionType.AGGREGATE) {
            runnerKey = fnDefMeta.getExecutionMode() == ExecutionMode.NATIVE
                        ? "NATIVE_AGG"
                        : "AGGREGATE";
        } else if (fnDefMeta.getType() == FunctionType.WINDOW) {
            runnerKey = fnDefMeta.getExecutionMode() == ExecutionMode.NATIVE
                        ? "NATIVE_WINDOW"
                        : "WINDOW";
        } else {
            throw new AstFunctionException("Unsupported type: " + fnDefMeta.getType());
        }

        var runner = runnerRegistry.get(runnerKey);

        if (runner == null) {
            throw new AstFunctionException("No runner found for: " + runnerKey);
        }

        return runner.run(reducer, ast, ctx, evaluator);
    }
}