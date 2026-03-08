package com.example.find_my_edge.workspace.config.chart;

import com.example.find_my_edge.analytics.ast.util.HasDependencies;
import com.example.find_my_edge.common.config.uiconfigs.AstConfig;
import com.example.find_my_edge.common.config.uiconfigs.ColorRuleConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeriesConfig implements HasDependencies {

    private String key;
    private String name;
    private String type; // number, string, etc

    private AstConfig ast;
    private String formula;
    private List<String> dependencies;

    private String format;
    private Integer decimals;

    private List<ColorRuleConfig> colorRules = new ArrayList<>();

    private Double value;

    private String color;
    private String markerColor;
    private String areaColor;
    private String label;

    @Override
    public String getId() {
        return this.key;
    }
}