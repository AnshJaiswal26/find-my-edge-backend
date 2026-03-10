package com.example.find_my_edge.workspace.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ChartCategory {
    SERIES,
    GROUP;

    @JsonValue
    public String toJson() {
        return this.name().toLowerCase();
    }

    @JsonCreator
    public static ChartCategory fromJson(String value) {
        return ChartCategory.valueOf(value.toUpperCase());
    }
}
