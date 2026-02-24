package com.example.find_my_edge.analytics.ast.context;

public interface EvaluationContext {

    Object getKeyValue(String key);

    SchemaType getSchemaType(String key);
}