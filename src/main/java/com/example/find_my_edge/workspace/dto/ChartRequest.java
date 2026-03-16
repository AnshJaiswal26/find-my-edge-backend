package com.example.find_my_edge.workspace.dto;

import com.example.find_my_edge.analytics.config.GroupConfig;
import com.example.find_my_edge.workspace.config.chart.SeriesConfig;
import com.example.find_my_edge.workspace.config.chart.XMetric;
import com.example.find_my_edge.workspace.enums.ChartMode;
import com.example.find_my_edge.workspace.enums.ChartType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class ChartRequest {
    private ChartType chartType;
    private Map<String, Object> layout;

    private GroupConfig groupSpec;

    private ChartMode mode;

    @JsonProperty("xMetric")
    private XMetric xMetric;

    private List<SeriesConfig> series;
}
