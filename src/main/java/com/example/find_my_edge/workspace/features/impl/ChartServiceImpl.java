package com.example.find_my_edge.workspace.features.impl;

import com.example.find_my_edge.analytics.compute.ChartComputeService;
import com.example.find_my_edge.analytics.engine.context.TradeContextBuilder;
import com.example.find_my_edge.analytics.model.ChartResult;
import com.example.find_my_edge.workspace.builder.ChartBuilder;
import com.example.find_my_edge.workspace.config.chart.ChartConfig;
import com.example.find_my_edge.workspace.config.page.PageConfig;
import com.example.find_my_edge.workspace.dto.ChartLayoutDto;
import com.example.find_my_edge.workspace.dto.ChartRequest;
import com.example.find_my_edge.workspace.dto.ChartResponse;
import com.example.find_my_edge.workspace.enums.ChartCategory;
import com.example.find_my_edge.workspace.enums.ChartMode;
import com.example.find_my_edge.workspace.exception.chart.ChartNotFoundException;
import com.example.find_my_edge.workspace.exception.chart.InvalidChartConfigException;
import com.example.find_my_edge.workspace.features.ChartService;
import com.example.find_my_edge.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChartServiceImpl implements ChartService {

    private final WorkspaceService workspaceService;

    private final ChartBuilder chartBuilder;

    private final ChartComputeService chartComputeService;

    private final TradeContextBuilder contextBuilder;

    @Override
    public ChartResponse create(String pageName, ChartRequest dto) {
        ChartConfig config = chartBuilder.buildChart(
                dto.getChartType(),
                dto.getLayout(),
                dto.getGroupSpec(),
                dto.getMode(),
                dto.getXMetric(),
                dto.getSeries()
        );

        validateChart(config);

        ChartResult chartResult = null;

        if (config.getCategory() == ChartCategory.PARTITION) {
            chartComputeService.computeSingleAggregateChart(config, contextBuilder.buildContext());
        }

        if (config.getMode() == ChartMode.GROUP_AGGREGATE) {
            chartResult =
                    chartComputeService.computeGroupAggregateChart(config, contextBuilder.buildContext());
        }

        workspaceService.getPageAndModify(
                page -> {
                    String chartId = config.getId();

                    if (page.getCharts().containsKey(chartId)) {
                        throw new InvalidChartConfigException("Chart already exists with id: " + chartId);
                    }

                    page.getCharts().put(chartId, config);
                    page.getChartOrder().add(chartId);
                }, pageName
        );

        return new ChartResponse(config, chartResult);
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
    public ChartConfig update(String pageName, String chartId, ChartConfig config) {
        validateChart(config);

        workspaceService.getPageAndModify(
                page -> {
                    if (!page.getCharts().containsKey(chartId)) {
                        throw new ChartNotFoundException(chartId);
                    }

                    page.getCharts().put(chartId, config);
                }, pageName
        );

        return config;
    }

    @Override
    public void delete(String pageName, String chartId) {
        workspaceService.getPageAndModify(
                page -> {
                    if (!page.getCharts().containsKey(chartId)) {
                        throw new ChartNotFoundException(chartId);
                    }

                    page.getCharts().remove(chartId);
                    page.getChartOrder().remove(chartId);

                    page.getGridLayout().remove(chartId);
                }, pageName
        );
    }

    @Override
    public ChartLayoutDto updateLayout(
            String pageName,
            String chartId,
            ChartLayoutDto dto
    ) {
        if (dto.getLayout() == null) {
            throw new IllegalArgumentException("Layout cannot be null");
        }

        workspaceService.getPageAndModify(
                page -> {
                    ChartConfig chart = getChartOrThrow(page, chartId);

                    chart.setLayout(dto.getLayout());
                    chart.setSeriesById(dto.getSeriesById());

                }, pageName
        );

        return dto;
    }

    private void validateChart(ChartConfig config) {

        if (config == null) {
            throw new InvalidChartConfigException("ChartConfig cannot be null");
        }

        if (config.getId() == null || config.getId().isBlank()) {
            throw new InvalidChartConfigException("Chart id cannot be null or blank");
        }

        if (config.getMode() == null && config.getCategory() == ChartCategory.CARTESIAN) {
            throw new InvalidChartConfigException("Chart mode is required for cartesian charts");
        }

        if (config.getSeriesOrder() == null || config.getSeriesOrder().isEmpty()) {
            throw new InvalidChartConfigException("Chart must contain at least one series");
        }
    }

    private ChartConfig getChartOrThrow(PageConfig page, String chartId) {
        ChartConfig chart = page.getCharts().get(chartId);
        if (chart == null) {
            throw new ChartNotFoundException(chartId);
        }
        return chart;
    }
}