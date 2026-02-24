package com.example.find_my_edge.analytics.ast.evaluator;

import com.example.find_my_edge.analytics.ast.context.EvaluationContext;
import com.example.find_my_edge.analytics.ast.function.FunctionHandler;
import com.example.find_my_edge.common.config.AstConfig;
import org.springframework.stereotype.Service;

@Service
public class AstEvaluator {

    private final BinaryHandler binaryHandler;
    private final UnaryHandler unaryHandler;
    private final FunctionHandler functionHandler;

    public AstEvaluator(BinaryHandler binaryHandler,
            UnaryHandler unaryHandler,
            FunctionHandler functionHandler) {
        this.binaryHandler = binaryHandler;
        this.unaryHandler = unaryHandler;
        this.functionHandler = functionHandler;
    }

    public Object evaluate(AstConfig ast, EvaluationContext ctx) {
        if (ast == null) return null;

        return switch (ast.getType()) {
            case "constant" -> ast.getValue();
            case "key" -> ctx.getKeyValue(ast.getKey());
            case "unary" -> unaryHandler.handle(ast, ctx, this);
            case "binary" -> binaryHandler.handle(ast, ctx, this);
            case "function" -> functionHandler.handle(ast, ctx);
            default -> null;
        };
    }
}