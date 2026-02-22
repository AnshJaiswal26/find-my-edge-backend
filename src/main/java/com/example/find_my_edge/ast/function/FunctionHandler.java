package com.example.find_my_edge.ast.function;

import com.example.find_my_edge.ast.context.EvaluationContext;
import com.example.find_my_edge.ast.reducer.Reducer;
import com.example.find_my_edge.common.dto.AstDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FunctionHandler {

    private final FunctionRegistry registry;

    public Object handle(AstDTO ast, EvaluationContext ctx) {

        Reducer fn = registry.get(ast.getFn());
        if (fn == null) return null;

        return fn.getExecutor().apply(ast, ctx);
    }
}