package com.example.find_my_edge.core.schema.dto;

import lombok.Data;

@Data
public class ColorRuleDto {
    private String operator;  // greaterThan, lessThan, equals
    private Double value;
    private Double value2;
    private String color;
}
