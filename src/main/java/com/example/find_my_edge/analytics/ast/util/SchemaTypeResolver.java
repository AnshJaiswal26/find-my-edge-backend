package com.example.find_my_edge.analytics.ast.util;

import com.example.find_my_edge.analytics.ast.context.SchemaType;
import com.example.find_my_edge.schema.model.Schema;

import java.util.HashMap;
import java.util.Map;

public final class SchemaTypeResolver {

    private SchemaTypeResolver() {}

    public static Map<String, SchemaType> buildSchemaTypeMap(Map<String, Schema> schemas) {

        Map<String, SchemaType> map = new HashMap<>();

        for (Schema s : schemas.values()) {
            if (s == null || s.getId() == null) continue;

            map.put(
                    s.getId(),
                    new SchemaType(
                            s.getDisplay() != null ? s.getDisplay().getFormat() : null,
                            s.getType() != null ? s.getType().toString() : null
                    )
            );
        }

        return map;
    }
}