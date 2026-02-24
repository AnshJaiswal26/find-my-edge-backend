package com.example.find_my_edge.workspace.features;

import com.example.find_my_edge.workspace.config.chart.ChartConfig;
import com.example.find_my_edge.workspace.config.chart.SeriesConfig;

import java.util.List;
import java.util.Map;

public interface ChartService {
    ChartConfig create(String page, ChartConfig dto);

    ChartConfig getById(String page, String chartId);

    Map<String, ChartConfig> getAll(String page);

    ChartConfig update(String page, String chartId, ChartConfig dto);

    void delete(String page, String chartId);

    Map<String, Object> updateLayout(String page, String chartId, Object layout);

    List<SeriesConfig> updateSeriesConfig(String page, String chartId, List<SeriesConfig> seriesConfig);

    List<String> updateOrder(String page, List<String> order);

}
