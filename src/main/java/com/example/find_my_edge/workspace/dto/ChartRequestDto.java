package com.example.find_my_edge.workspace.dto;

import com.example.find_my_edge.workspace.config.chart.SeriesConfig;
import com.example.find_my_edge.workspace.config.chart.XMetric;
import com.example.find_my_edge.workspace.enums.ChartType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class ChartRequestDto {
    private ChartType chartType;
    private Map<String, Object> layout;

    @JsonProperty("xMetric")
    private XMetric xMetric;

    private Map<String, SeriesConfig> seriesById;
    private List<String> seriesOrder;
}
