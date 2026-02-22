package com.example.find_my_edge.ast.util;

import com.example.find_my_edge.ast.context.SchemaType;

public class NormalizeUtil {

    public static Object[] normalize(Object left, Object right, SchemaType schemaType) {

        if (left instanceof Number && right instanceof String) {
            return new Object[]{left, DeformatUtil.parseWithCache((String) right, schemaType)};
        }

        if (right instanceof Number && left instanceof String) {
            return new Object[]{DeformatUtil.parseWithCache((String) left, schemaType), right};
        }

        return new Object[]{left, right};
    }
}