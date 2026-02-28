package com.example.find_my_edge.analytics.ast.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ValueType {
    DATE, TIME, DATETIME, DURATION, STRING, BOOLEAN, ANY, NUMBER;

    @JsonValue
    public String toJson() {
        return this.name().toLowerCase();
    }

    @JsonCreator
    public static ValueType fromJson(String value) {
        return ValueType.valueOf(value.toUpperCase());
    }
}
