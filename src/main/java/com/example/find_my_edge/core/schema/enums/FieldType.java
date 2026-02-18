package com.example.find_my_edge.core.schema.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum FieldType {
    NUMBER, DURATION, DATE, TIME, DATETIME, TEXT, SELECT, BOOLEAN;

    @JsonValue
    public String toJson() {
        return this.name().toLowerCase();
    }
}
