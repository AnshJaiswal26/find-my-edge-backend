package com.example.find_my_edge.analytics.config;

import com.example.find_my_edge.common.config.AstConfig;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class GroupConfig {

    private String type;
    private String key;

    private String unit;
    private List<GroupRangeConfig> ranges;

    private String operator;
    private double value;
    private double valueTo;
    private Map<String, String> labels;

    private AstConfig ast;

}
