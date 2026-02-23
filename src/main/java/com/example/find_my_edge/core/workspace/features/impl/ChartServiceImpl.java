package com.example.find_my_edge.core.workspace.features.impl;

import com.example.find_my_edge.core.workspace.dto.chart.ChartDTO;
import com.example.find_my_edge.core.workspace.dto.chart.SeriesConfigDTO;
import com.example.find_my_edge.core.workspace.dto.core.PageDTO;
import com.example.find_my_edge.core.workspace.entity.Workspace;
import com.example.find_my_edge.core.workspace.model.WorkspaceData;
import com.example.find_my_edge.core.workspace.features.ChartService;
import com.example.find_my_edge.core.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChartServiceImpl implements ChartService {

    private final WorkspaceService workspaceService;

    @Override
    public ChartDTO create(Long workspaceId, String page, ChartDTO dto) {
        PageDTO pageDTO = workspaceService.getPage(workspaceId, page);

        return pageDTO.getCharts().put(dto.getMeta().getId(), dto);
    }

    @Override
    public ChartDTO getById(Long workspaceId, String page, String chartId) {
        PageDTO pageDTO = workspaceService.getPage(workspaceId, page);

        return pageDTO.getCharts().get(chartId);
    }

    @Override
    public Map<String, ChartDTO> getAll(Long workspaceId, String page) {
        PageDTO pageDTO = workspaceService.getPage(workspaceId, page);

        return pageDTO.getCharts();
    }

    @Override
    public ChartDTO update(Long workspaceId, String page, String chartId, ChartDTO dto) {
        PageDTO pageDTO = workspaceService.getPage(workspaceId, page);

        return pageDTO.getCharts().put(chartId, dto);
    }

    @Override
    public void delete(Long workspaceId, String page, String chartId) {
        PageDTO pageDTO = workspaceService.getPage(workspaceId, page);

        pageDTO.getCharts().remove(chartId);
    }

    @Override
    public Map<String, Object> updateLayout(Long workspaceId, String page, String chartId, Object layout) {
        PageDTO pageDTO = workspaceService.getPage(workspaceId, page);

        return pageDTO.getCharts().get(chartId).getLayout();
    }

    @Override
    public List<SeriesConfigDTO> updateSeriesConfig(Long workspaceId, String page, String chartId, List<SeriesConfigDTO> seriesConfig) {
        PageDTO pageDTO = workspaceService.getPage(workspaceId, page);

        ChartDTO chartDTO = pageDTO.getCharts().get(chartId);

        if (chartDTO.getMeta().getCategory().equals("series")) {
            chartDTO.setYSeriesConfig(seriesConfig);
        } else {
            chartDTO.setSeriesConfig(seriesConfig);
        }

        return seriesConfig;
    }

    @Override
    public List<String> updateOrder(Long workspaceId, String page, List<String> order) {
        PageDTO pageDTO = workspaceService.getPage(workspaceId, page);

        pageDTO.setChartOrder(order);
        return order;
    }
}
