package com.example.find_my_edge.workspace.config.chart;

import com.example.find_my_edge.analytics.config.FilterConfig;
import com.example.find_my_edge.analytics.config.GroupConfig;
import com.example.find_my_edge.analytics.config.GroupRangeConfig;
import com.example.find_my_edge.analytics.config.SortConfig;
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
    private GroupRangeConfig selection;
    private SeriesConfig xSeriesConfig;
    private List<SeriesConfig> ySeriesConfig = new ArrayList<>();
    private List<SeriesConfig> seriesConfig = new ArrayList<>();
}
