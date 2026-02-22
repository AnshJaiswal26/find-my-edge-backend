package com.example.find_my_edge.ast.evaluator;

import com.example.find_my_edge.ast.context.EvaluationContext;
import com.example.find_my_edge.ast.function.FunctionHandler;
import com.example.find_my_edge.common.dto.AstDTO;
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

    public Object evaluate(AstDTO ast, EvaluationContext ctx) {
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