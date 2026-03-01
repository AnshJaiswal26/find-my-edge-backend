package com.example.find_my_edge.workspace.features.impl;

import com.example.find_my_edge.workspace.config.chart.ChartConfig;
import com.example.find_my_edge.workspace.config.chart.SeriesConfig;
import com.example.find_my_edge.workspace.config.page.PageConfig;
import com.example.find_my_edge.workspace.enums.ChartCategory;
import com.example.find_my_edge.workspace.exception.chart.ChartNotFoundException;
import com.example.find_my_edge.workspace.exception.chart.InvalidChartConfigException;
import com.example.find_my_edge.workspace.features.ChartService;
import com.example.find_my_edge.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChartServiceImpl implements ChartService {

    private final WorkspaceService workspaceService;

    @Override
    public ChartConfig create(String pageName, ChartConfig dto) {
        validateChart(dto);

        workspaceService.getPageAndModify(page -> {
            String chartId = dto.getMeta().getId();

            if (page.getCharts().containsKey(chartId)) {
                throw new InvalidChartConfigException("Chart already exists with id: " + chartId);
            }

            page.getCharts().put(chartId, dto);
        }, pageName);

        return dto;
    }

    @Override
    public ChartConfig getById(String pageName, String chartId) {
        PageConfig page = workspaceService.getPage(pageName);
        return getChartOrThrow(page, chartId);
    }

    @Override
    public Map<String, ChartConfig> getAll(String pageName) {
        PageConfig page = workspaceService.getPage(pageName);
        return Collections.unmodifiableMap(page.getCharts());
    }

    @Override
    public ChartConfig update(String pageName, String chartId, ChartConfig dto) {
        validateChart(dto);

        workspaceService.getPageAndModify(page -> {
            if (!page.getCharts().containsKey(chartId)) {
                throw new ChartNotFoundException(chartId);
            }

            page.getCharts().put(chartId, dto);
        }, pageName);

        return dto;
    }

    @Override
    public void delete(String pageName, String chartId) {
        workspaceService.getPageAndModify(page -> {
            if (!page.getCharts().containsKey(chartId)) {
                throw new ChartNotFoundException(chartId);
            }

            page.getCharts().remove(chartId);
        }, pageName);
    }

    @Override
    public Map<String, Object> updateLayout(
            String pageName,
            String chartId,
            Map<String, Object> layout
    ) {
        if (layout == null) {
            throw new IllegalArgumentException("Layout cannot be null");
        }

        workspaceService.getPageAndModify(page -> {
            ChartConfig chart = getChartOrThrow(page, chartId);
            chart.setLayout(layout);
        }, pageName);

        return layout;
    }

    @Override
    public List<SeriesConfig> updateSeriesConfig(
            String pageName,
            String chartId,
            List<SeriesConfig> seriesConfig
    ) {
        if (seriesConfig == null) {
            throw new IllegalArgumentException("Series config cannot be null");
        }

        workspaceService.getPageAndModify(page -> {
            ChartConfig chart = getChartOrThrow(page, chartId);

            if (chart.getMeta().getCategory().equals(ChartCategory.SERIES.key())) {
                chart.setYSeries(seriesConfig);
            } else {
                chart.setSeriesConfig(seriesConfig);
            }
        }, pageName);

        return seriesConfig;
    }

    // =========================
    // 🔒 PRIVATE HELPERS
    // =========================

    private ChartConfig getChartOrThrow(PageConfig page, String chartId) {
        ChartConfig chart = page.getCharts().get(chartId);
        if (chart == null) {
            throw new ChartNotFoundException(chartId);
        }
        return chart;
    }

    private void validateChart(ChartConfig dto) {
        if (dto == null) {
            throw new InvalidChartConfigException("ChartConfig cannot be null");
        }

        if (dto.getMeta() == null) {
            throw new InvalidChartConfigException("Chart meta cannot be null");
        }

        if (dto.getMeta().getId() == null || dto.getMeta().getId().isBlank()) {
            throw new InvalidChartConfigException("Chart id cannot be null or blank");
        }

        if (dto.getMeta().getCategory() == null) {
            throw new InvalidChartConfigException("Chart category cannot be null");
        }
    }
}