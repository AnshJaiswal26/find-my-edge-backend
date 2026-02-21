package com.example.find_my_edge.core.workspace.features;

import com.example.find_my_edge.core.workspace.dto.chart.ChartDTO;
import com.example.find_my_edge.core.workspace.dto.chart.SeriesConfigDTO;

import java.util.List;
import java.util.Map;

public interface ChartService {
    ChartDTO addChart(String page, ChartDTO dto);

    ChartDTO getChartById(String page, String chartId);

    Map<String, ChartDTO> getAll(String page);

    ChartDTO updateChart(String page, String chartId, ChartDTO dto);

    void deleteChart(String page, String chartId);

    Map<String, Object> updateLayout(String page, String chartId, Object layout);

    List<SeriesConfigDTO> updateSeriesConfig(String page, String chartId, List<SeriesConfigDTO> seriesConfig);

    List<String> updateChartOrder(String page, List<String> order);

}
