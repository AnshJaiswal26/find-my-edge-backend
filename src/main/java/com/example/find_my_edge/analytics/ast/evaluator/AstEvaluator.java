package com.example.find_my_edge.analytics.ast.evaluator;

import com.example.find_my_edge.analytics.ast.context.EvaluationContext;
import com.example.find_my_edge.analytics.ast.enums.NodeType;
import com.example.find_my_edge.analytics.ast.function.FunctionHandler;
import com.example.find_my_edge.analytics.ast.model.AstNode;
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

    public Object evaluate(AstNode ast, EvaluationContext ctx) {
        if (ast == null) return null;

        return switch (ast.getType()) {
            case NodeType.CONSTANT -> ast.getValue();
            case NodeType.IDENTIFIER -> ctx.getKeyValue(ast.getField());
            case NodeType.UNARY -> unaryHandler.handle(ast, ctx, this);
            case NodeType.BINARY -> binaryHandler.handle(ast, ctx, this);
            case NodeType.FUNCTION -> functionHandler.handle(ast, ctx, this);
            default -> null;
        };
    }
}