package com.example.find_my_edge.analytics.ast.evaluator;

import com.example.find_my_edge.analytics.ast.context.EvaluationContext;
import com.example.find_my_edge.analytics.ast.util.TypeUtil;
import com.example.find_my_edge.common.config.AstConfig;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BinaryHandler {

    private final ComparisonHandler comparisonHandler;

    public Object handle(AstConfig ast, EvaluationContext ctx, AstEvaluator evaluator) {

        Object left = evaluator.evaluate(ast.getLeft(), ctx);
        Object right = evaluator.evaluate(ast.getRight(), ctx);

        if (left == null || right == null) return null;

        return switch (ast.getOp()) {
            case "+" -> TypeUtil.toDouble(left) + TypeUtil.toDouble(right);
            case "-" -> TypeUtil.toDouble(left) - TypeUtil.toDouble(right);
            case "*" -> TypeUtil.toDouble(left) * TypeUtil.toDouble(right);
            case "/" -> TypeUtil.toDouble(right) == 0 ? null :
                        TypeUtil.toDouble(left) / TypeUtil.toDouble(right);

            case "AND" -> (TypeUtil.toBoolean(left) && TypeUtil.toBoolean(right)) ? 1 : 0;
            case "OR" -> (TypeUtil.toBoolean(left) || TypeUtil.toBoolean(right)) ? 1 : 0;

            case "==", "!=", ">", "<", ">=", "<=" -> comparisonHandler.handle(ast, ctx, evaluator);

            default -> null;
        };
    }
}