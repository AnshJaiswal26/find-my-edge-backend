package com.example.find_my_edge.workspace.config.chart;
import com.example.find_my_edge.common.config.AstConfig;
import com.example.find_my_edge.common.config.ColorRuleConfig;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SeriesConfig {

    private String key;
    private String name;
    private String type; // number, string, etc
    private AstConfig ast;

    private String format;
    private Integer decimals;

    private List<ColorRuleConfig> colorRules = new ArrayList<>();

    private String color;
    private String markerColor;
    private String areaColor;
    private String label;

}