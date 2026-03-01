package com.example.find_my_edge.analytics.config;

import lombok.Data;

@Data
public class FilterConfig {

    public FilterConfig(Double value, Double from, Double to) {
        this.value = value;
        this.from = from;
        this.to = to;
    }

    private String key;
    private String operator;
    private Double value;
    private Double from;
    private Double to;
}
