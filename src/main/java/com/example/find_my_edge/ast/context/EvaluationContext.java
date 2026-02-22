package com.example.find_my_edge.ast.context;

public interface EvaluationContext {

    Object getKeyValue(String key);

    SchemaType getSchemaType(String key);
}