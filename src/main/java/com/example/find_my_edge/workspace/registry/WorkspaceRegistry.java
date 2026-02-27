package com.example.find_my_edge.workspace.registry;

import com.example.find_my_edge.workspace.config.page.PageConfig;
import com.example.find_my_edge.workspace.entity.WorkspaceEntity;
import com.example.find_my_edge.workspace.enums.PageType;
import com.example.find_my_edge.workspace.model.WorkspaceData;
import com.example.find_my_edge.workspace.service.WorkspaceService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
@RequiredArgsConstructor
public class WorkspaceRegistry {

    private final ChartRegistry chartRegistry;

    private final StatRegistry statRegistry;

    private final WorkspaceService workspaceService;

    @PostConstruct
    public void init(){
        workspaceService.delete();
        WorkspaceEntity workspaceEntity = workspaceService.get();
        workspaceEntity.setData(createDefaultWorkspace());
    }

    public WorkspaceData createDefaultWorkspace() {

        WorkspaceData data = new WorkspaceData();

        Map<String, PageConfig> pages = new HashMap<>();

        pages.put(PageType.DASHBOARD.key(), defaultDashboard());

        data.setPages(pages);

        return data;
    }

    private PageConfig defaultDashboard() {

        PageConfig page = new PageConfig();

        // default charts
        page.setCharts(chartRegistry.getChartsById());
        page.setChartOrder(chartRegistry.getOrder());

        // default stats
        page.setStatsById(statRegistry.getStatsById());
        page.setStatsOrder(statRegistry.getOrder());

        return page;
    }
}