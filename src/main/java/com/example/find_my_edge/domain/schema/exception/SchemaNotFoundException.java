package com.example.find_my_edge.domain.schema.exception;

public class SchemaNotFoundException extends SchemaException {
    public SchemaNotFoundException(String id) {
        super("Schema not found: " + id);
    }
}
