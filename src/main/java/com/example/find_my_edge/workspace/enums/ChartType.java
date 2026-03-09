package com.example.find_my_edge.workspace.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ChartType {
    BAR, LINE, DONUT, RADIAL_BAR, RADAR, POLAR_AREA;

    @JsonCreator
    public static ChartType fromJson(String value) {
        return ChartType.valueOf(value.replaceAll("([a-z])([A-Z])", "$1_$2").toUpperCase());
    }

    @JsonValue
    public String toJson() {
        String[] parts = name().toLowerCase().split("_");

        StringBuilder sb = new StringBuilder(parts[0]);

        for (int i = 1; i < parts.length; i++) {
            sb.append(Character.toUpperCase(parts[i].charAt(0)))
              .append(parts[i].substring(1));
        }

        return sb.toString();
    }
}