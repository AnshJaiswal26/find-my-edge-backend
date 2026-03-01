package com.example.find_my_edge.workspace.config.chart;

import com.example.find_my_edge.analytics.config.FilterConfig;
import com.example.find_my_edge.analytics.config.GroupConfig;
import com.example.find_my_edge.analytics.config.SortConfig;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ChartConfig {
    private ChartMetaConfig meta;
    private Map<String, Object> layout = new HashMap<>();
    private SortConfig sort;
    private GroupConfig groupSpec;
    private List<FilterConfig> filters = new ArrayList<>();
    private SelectionConfig selection;

    @JsonProperty("xSeriesConfig")
    private SeriesConfig xSeries;

    @JsonProperty("ySeriesConfig")
    private List<SeriesConfig> ySeries;
    private List<SeriesConfig> seriesConfig;
}
