package com.example.find_my_edge.core.schema.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SchemaSource {
    SYSTEM, USER, COMPUTED;

    @JsonValue
    public String toJson() {
        return this.name().toLowerCase();
    }

    @JsonCreator
    public static ComputeMode fromJson(String value) {
        return ComputeMode.valueOf(value.toUpperCase());
    }
}
