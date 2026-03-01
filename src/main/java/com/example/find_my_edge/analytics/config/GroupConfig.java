package com.example.find_my_edge.analytics.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupConfig {

    private String type;
    private String key;

    private String unit;
    private List<GroupRangeConfig> ranges;

    private String operator;
    private Object value;
    private Double from;
    private Double to;

    private Map<String, String> labels;
}
