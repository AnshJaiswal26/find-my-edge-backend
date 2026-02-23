package com.example.find_my_edge.core.workspace.features;

import com.example.find_my_edge.core.workspace.dto.chart.ChartDTO;
import com.example.find_my_edge.core.workspace.dto.chart.SeriesConfigDTO;

import java.util.List;
import java.util.Map;

public interface ChartService {
    ChartDTO create(Long workspaceId, String page, ChartDTO dto);

    ChartDTO getById(Long workspaceId, String page, String chartId);

    Map<String, ChartDTO> getAll(Long workspaceId, String page);

    ChartDTO update(Long workspaceId, String page, String chartId, ChartDTO dto);

    void delete(Long workspaceId, String page, String chartId);

    Map<String, Object> updateLayout(Long workspaceId, String page, String chartId, Object layout);

    List<SeriesConfigDTO> updateSeriesConfig(Long workspaceId, String page, String chartId, List<SeriesConfigDTO> seriesConfig);

    List<String> updateOrder(Long workspaceId, String page, List<String> order);

}
