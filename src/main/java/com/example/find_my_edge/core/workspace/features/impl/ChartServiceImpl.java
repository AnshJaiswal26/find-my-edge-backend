package com.example.find_my_edge.core.workspace.features.impl;

import com.example.find_my_edge.core.workspace.dto.chart.ChartDTO;
import com.example.find_my_edge.core.workspace.dto.chart.SeriesConfigDTO;
import com.example.find_my_edge.core.workspace.dto.core.WorkspaceDTO;
import com.example.find_my_edge.core.workspace.features.ChartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChartServiceImpl implements ChartService {

    private final WorkspaceDTO workspace;

    @Override
    public ChartDTO addChart(String page, ChartDTO dto) {
        return workspace.getPages().get(page).getCharts().put(dto.getMeta().getId(), dto);
    }

    @Override
    public ChartDTO getChartById(String page, String chartId) {
        return workspace.getPages().get(page).getCharts().get(chartId);
    }

    @Override
    public Map<String, ChartDTO> getAll(String page) {
        return workspace.getPages().get(page).getCharts();
    }

    @Override
    public ChartDTO updateChart(String page, String chartId, ChartDTO dto) {
        return workspace.getPages().get(page).getCharts().put(chartId, dto);
    }

    @Override
    public void deleteChart(String page, String chartId) {
        workspace.getPages().get(page).getCharts().remove(chartId);
    }

    @Override
    public Map<String, Object> updateLayout(String page, String chartId, Object layout) {
        return workspace.getPages().get(page).getCharts().get(chartId).getLayout();
    }

    @Override
    public List<SeriesConfigDTO> updateSeriesConfig(String page, String chartId, List<SeriesConfigDTO> seriesConfig) {
        ChartDTO chartDTO = workspace.getPages().get(page).getCharts().get(chartId);

        if (chartDTO.getMeta().getCategory().equals("series")) {
            chartDTO.setYSeriesConfig(seriesConfig);
        } else {
            chartDTO.setSeriesConfig(seriesConfig);
        }

        return seriesConfig;
    }

    @Override
    public List<String> updateChartOrder(String page, List<String> order) {
        workspace.getPages().get(page).setChartOrder(order);
        return order;
    }
}
