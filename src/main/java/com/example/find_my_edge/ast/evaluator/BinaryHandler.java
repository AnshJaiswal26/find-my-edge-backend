package com.example.find_my_edge.ast.evaluator;

import com.example.find_my_edge.ast.context.EvaluationContext;
import com.example.find_my_edge.ast.util.TypeUtil;
import com.example.find_my_edge.common.dto.AstDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BinaryHandler {

    private final ComparisonHandler comparisonHandler;

    public Object handle(AstDTO ast, EvaluationContext ctx, AstEvaluator evaluator) {

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