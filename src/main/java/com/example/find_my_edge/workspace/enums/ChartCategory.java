package com.example.find_my_edge.workspace.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ChartCategory {
    SERIES("series"),
    GROUP("group");

    private final String key;

    ChartCategory(String key) {
        this.key = key;
    }

    @JsonValue
    public String toJson() {
        return key;
    }

    public String key(){
        return key;
    }

    @JsonCreator
    public static ChartCategory fromJson(String value) {
        for (ChartCategory chartCategory : ChartCategory.values()) {
            if (chartCategory.key.equalsIgnoreCase(value)) {
                return chartCategory;
            }
        }
        throw new IllegalArgumentException("Invalid Page: " + value);
    }
}
