package com.example.find_my_edge.analytics.config;

import lombok.Data;

@Data
public class FilterConfig {

    public FilterConfig(Object value, Double from, Double to) {
        this.value = value;
        this.from = from;
        this.to = to;
    }

    private String key;
    private String operator;
    private Object value;
    private Double from;
    private Double to;
}
