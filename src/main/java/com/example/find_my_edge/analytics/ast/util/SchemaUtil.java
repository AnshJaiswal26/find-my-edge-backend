package com.example.find_my_edge.analytics.ast.util;

import com.example.find_my_edge.analytics.ast.context.EvaluationContext;
import com.example.find_my_edge.analytics.ast.context.SchemaType;
import com.example.find_my_edge.analytics.ast.model.AstNode;
import com.example.find_my_edge.common.config.AstConfig;

public class SchemaUtil {

    public static SchemaType resolve(AstNode ast, EvaluationContext ctx) {
        SchemaType left = get(ast.getLeft(), ctx);
        SchemaType right = get(ast.getRight(), ctx);
        return left != null ? left : right;
    }

    private static SchemaType get(AstNode node, EvaluationContext ctx) {
        if (!"key".equals(node.getType())) return null;
        return ctx.getSchemaType(node.getKey());
    }
}