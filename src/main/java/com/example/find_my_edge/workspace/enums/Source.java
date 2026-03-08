package com.example.find_my_edge.workspace.enums;

import com.example.find_my_edge.schema.enums.SchemaSource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Source {
    USER, SYSTEM;

    @JsonValue
    public String toJson() {
        return this.name().toLowerCase();
    }

    @JsonCreator
    public static SchemaSource fromJson(String value) {
        return SchemaSource.valueOf(value.toUpperCase());
    }
}
