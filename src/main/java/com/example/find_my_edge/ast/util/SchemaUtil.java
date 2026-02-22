package com.example.find_my_edge.ast.util;

import com.example.find_my_edge.ast.context.EvaluationContext;
import com.example.find_my_edge.ast.context.SchemaType;
import com.example.find_my_edge.common.dto.AstDTO;

public class SchemaUtil {

    public static SchemaType resolve(AstDTO ast, EvaluationContext ctx) {
        SchemaType left = get(ast.getLeft(), ctx);
        SchemaType right = get(ast.getRight(), ctx);
        return left != null ? left : right;
    }

    private static SchemaType get(AstDTO node, EvaluationContext ctx) {
        if (!"key".equals(node.getType())) return null;
        return ctx.getSchemaType(node.getKey());
    }
}