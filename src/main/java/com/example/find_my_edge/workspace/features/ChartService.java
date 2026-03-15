package com.example.find_my_edge.workspace.features;

import com.example.find_my_edge.workspace.config.chart.ChartConfig;
import com.example.find_my_edge.workspace.dto.ChartLayoutDto;
import com.example.find_my_edge.workspace.dto.ChartRequest;
import com.example.find_my_edge.workspace.dto.ChartResponse;

import java.util.Map;

public interface ChartService {
    ChartResponse create(String page, ChartRequest dto);

    ChartConfig getById(String page, String chartId);

    Map<String, ChartConfig> getAll(String page);

    ChartConfig update(String page, String chartId, ChartConfig dto);

    void delete(String page, String chartId);

    ChartLayoutDto updateLayout(String page, String chartId, ChartLayoutDto dto);
}
