package com.example.find_my_edge.domain.schema.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SchemaRole {
    SYSTEM_REQUIRED,
    SYSTEM_OPTIONAL,
    USER_DEFINED;

    @JsonValue
    public String toJson() {
        return this.name().toLowerCase();
    }

    @JsonCreator
    public static SchemaRole fromJson(String value) {
        return SchemaRole.valueOf(value.toUpperCase());
    }
}
