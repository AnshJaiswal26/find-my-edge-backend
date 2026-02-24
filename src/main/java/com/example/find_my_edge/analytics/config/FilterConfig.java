package com.example.find_my_edge.analytics.config;

import lombok.Data;

@Data
public class FilterConfig {
    private String key;
    private String operator;
    private Double value;
    private Double from;
    private Double to;
}
