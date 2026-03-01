package com.example.find_my_edge.common.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ColorRuleConfig {
    private String operator;  // greaterThan, lessThan, equals
    private Double value;
    private Double from;
    private Double to;
    private String color;
    private String label;
}
