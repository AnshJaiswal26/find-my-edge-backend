package com.example.find_my_edge.workspace.features.impl;

import com.example.find_my_edge.workspace.config.chart.ChartConfig;
import com.example.find_my_edge.workspace.config.chart.SeriesConfig;
import com.example.find_my_edge.workspace.config.page.PageConfig;
import com.example.find_my_edge.workspace.features.ChartService;
import com.example.find_my_edge.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChartServiceImpl implements ChartService {

    private final WorkspaceService workspaceService;

    @Override
    public ChartConfig create(String page, ChartConfig dto) {
        PageConfig pageConfig = workspaceService.getPage(page);

        return pageConfig.getCharts().put(dto.getMeta().getId(), dto);
    }

    @Override
    public ChartConfig getById(String page, String chartId) {
        PageConfig pageConfig = workspaceService.getPage(page);

        return pageConfig.getCharts().get(chartId);
    }

    @Override
    public Map<String, ChartConfig> getAll(String page) {
        PageConfig pageConfig = workspaceService.getPage(page);

        return pageConfig.getCharts();
    }

    @Override
    public ChartConfig update(String page, String chartId, ChartConfig dto) {
        PageConfig pageConfig = workspaceService.getPage(page);

        return pageConfig.getCharts().put(chartId, dto);
    }

    @Override
    public void delete(String page, String chartId) {
        PageConfig pageConfig = workspaceService.getPage(page);

        pageConfig.getCharts().remove(chartId);
    }

    @Override
    public Map<String, Object> updateLayout(String page, String chartId, Object layout) {
        PageConfig pageConfig = workspaceService.getPage(page);

        return pageConfig.getCharts().get(chartId).getLayout();
    }

    @Override
    public List<SeriesConfig> updateSeriesConfig(String page, String chartId, List<SeriesConfig> seriesConfig) {
        PageConfig pageConfig = workspaceService.getPage(page);

        ChartConfig chartConfig = pageConfig.getCharts().get(chartId);

        if (chartConfig.getMeta().getCategory().equals("series")) {
            chartConfig.setYSeriesConfig(seriesConfig);
        } else {
            chartConfig.setSeriesConfig(seriesConfig);
        }

        return seriesConfig;
    }

    @Override
    public List<String> updateOrder(String page, List<String> order) {
        PageConfig pageConfig = workspaceService.getPage(page);

        pageConfig.setChartOrder(order);
        return order;
    }
}
