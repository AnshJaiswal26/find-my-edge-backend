package com.example.find_my_edge.domain.schema.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FieldType {
    NUMBER, DURATION, DATE, TIME, DATETIME, TEXT, SELECT, BOOLEAN;

    @JsonValue
    public String toJson() {
        return this.name().toLowerCase();
    }

    @JsonCreator
    public static ComputeMode fromJson(String value) {
        return ComputeMode.valueOf(value.toUpperCase());
    }
}
