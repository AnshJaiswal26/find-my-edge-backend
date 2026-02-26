package com.example.find_my_edge.analytics.ast.evaluator;

import com.example.find_my_edge.analytics.ast.context.EvaluationContext;
import com.example.find_my_edge.analytics.ast.model.AstNode;
import com.example.find_my_edge.analytics.ast.util.TypeUtil;
import com.example.find_my_edge.common.config.AstConfig;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BinaryHandler {

    private final ComparisonHandler comparisonHandler;

    public Object handle(AstNode ast, EvaluationContext ctx, AstEvaluator evaluator) {

        Object left = evaluator.evaluate(ast.getLeft(), ctx);
        Object right = evaluator.evaluate(ast.getRight(), ctx);

//        System.out.println("left -> " + left);
//        System.out.println("right -> " + right);


        if (left == null || right == null) return null;

        return switch (ast.getOp()) {
            case "+" -> TypeUtil.toDouble(left) + TypeUtil.toDouble(right);
            case "-" -> TypeUtil.toDouble(left) - TypeUtil.toDouble(right);
            case "*" -> TypeUtil.toDouble(left) * TypeUtil.toDouble(right);
            case "/" -> TypeUtil.toDouble(right) == 0 ? null :
                        TypeUtil.toDouble(left) / TypeUtil.toDouble(right);

            case "AND" -> TypeUtil.toBoolean(left) && TypeUtil.toBoolean(right);
            case "OR" -> TypeUtil.toBoolean(left) || TypeUtil.toBoolean(right);

            case "==", "!=", ">", "<", ">=", "<=" -> comparisonHandler.handle(ast, ctx, evaluator);

            default -> null;
        };
    }
}