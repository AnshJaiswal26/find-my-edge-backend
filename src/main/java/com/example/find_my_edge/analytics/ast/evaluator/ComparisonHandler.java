package com.example.find_my_edge.analytics.ast.evaluator;

import com.example.find_my_edge.analytics.ast.context.EvaluationContext;
import com.example.find_my_edge.analytics.ast.context.SchemaType;
import com.example.find_my_edge.analytics.ast.model.AstNode;
import com.example.find_my_edge.analytics.ast.util.NormalizeUtil;
import com.example.find_my_edge.analytics.ast.util.SchemaUtil;
import com.example.find_my_edge.analytics.ast.util.TypeUtil;
import com.example.find_my_edge.common.config.AstConfig;
import org.springframework.stereotype.Service;

@Service
public class ComparisonHandler {

    public Object handle(AstNode ast, EvaluationContext ctx, AstEvaluator evaluator) {

        Object l = evaluator.evaluate(ast.getLeft(), ctx);
        Object r = evaluator.evaluate(ast.getRight(), ctx);

        if (l == null || r == null) return null;

        SchemaType schemaType = SchemaUtil.resolve(ast, ctx);

        if (schemaType != null) {
            Object[] normalized = NormalizeUtil.normalize(l, r, schemaType);
            l = normalized[0];
            r = normalized[1];
        }

        return switch (ast.getOp()) {
            case "==" -> l.equals(r) ? 1 : 0;
            case "!=" -> !l.equals(r) ? 1 : 0;
            case ">" -> TypeUtil.toDouble(l) > TypeUtil.toDouble(r) ? 1 : 0;
            case "<" -> TypeUtil.toDouble(l) < TypeUtil.toDouble(r) ? 1 : 0;
            case ">=" -> TypeUtil.toDouble(l) >= TypeUtil.toDouble(r) ? 1 : 0;
            case "<=" -> TypeUtil.toDouble(l) <= TypeUtil.toDouble(r) ? 1 : 0;
            default -> null;
        };
    }
}