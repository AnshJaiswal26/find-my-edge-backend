package com.example.find_my_edge.analytics.ast.function;

import com.example.find_my_edge.analytics.ast.context.EvaluationContext;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import com.example.find_my_edge.common.config.AstConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FunctionHandler {

    private final FunctionRegistry registry;

    public Object handle(AstConfig ast, EvaluationContext ctx) {

        Reducer fn = registry.get(ast.getFn());
        if (fn == null) return null;

        return fn.getExecutor().apply(ast, ctx);
    }
}