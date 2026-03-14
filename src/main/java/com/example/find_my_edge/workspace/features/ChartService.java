package com.example.find_my_edge.workspace.features;

import com.example.find_my_edge.workspace.config.chart.ChartConfig;
import com.example.find_my_edge.workspace.config.chart.SeriesConfig;
import com.example.find_my_edge.workspace.dto.ChartRequestDto;

import java.util.Map;

public interface ChartService {
    ChartConfig create(String page, ChartRequestDto dto);

    ChartConfig getById(String page, String chartId);

    Map<String, ChartConfig> getAll(String page);

    ChartConfig update(String page, String chartId, ChartConfig dto);

    void delete(String page, String chartId);

    Map<String, Object> updateLayout(String page, String chartId, Map<String, Object> layout);

    Map<String, SeriesConfig> updateSeriesConfig(String page, String chartId, Map<String, SeriesConfig> seriesConfig);
}
