package com.example.find_my_edge.workspace.features.impl;

import com.example.find_my_edge.workspace.config.page.PageConfig;
import com.example.find_my_edge.workspace.config.stat.StatConfig;
import com.example.find_my_edge.workspace.entity.WorkspaceEntity;
import com.example.find_my_edge.workspace.features.StatService;
import com.example.find_my_edge.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final WorkspaceService workspaceService;

    /* ---------------- GET ALL ---------------- */
    @Override
    public Map<String, Object> getAll(String page) {
        PageConfig pageConfig = workspaceService.getPage(page);
        System.out.println(pageConfig);
        return Map.of(
                "statsById", pageConfig.getStatsById(),
                "statsOrder", pageConfig.getStatsOrder()
        );
    }

    /* ---------------- GET BY id ---------------- */
    @Override
    public StatConfig getById(String page, String id) {
        PageConfig pageConfig = workspaceService.getPage(page);
        return pageConfig.getStatsById().get(id);
    }

    /* ---------------- ADD ---------------- */
    @Override
    public StatConfig create(String page, StatConfig stat) {
        WorkspaceEntity workspaceEntity = workspaceService.get();

        PageConfig pageConfig = workspaceEntity.getData().getPages().get(page);

        pageConfig.getStatsById().put(stat.getId(), stat);
        pageConfig.getStatsOrder().add(stat.getId());

        workspaceService.save(workspaceEntity);
        return stat;
    }

    @Override
    public void createAll(Map<String, StatConfig> stats, List<String> statsOrder) {
        WorkspaceEntity workspaceEntity = workspaceService.get();

        PageConfig pageConfig = workspaceEntity
                .getData()
                .getPages()
                .computeIfAbsent("dashboard", p -> new PageConfig());


        pageConfig.getStatsById().putAll(stats);
        pageConfig.getStatsOrder().addAll(statsOrder);

        System.out.println(pageConfig);
        workspaceService.save(workspaceEntity);
    }

    /* ---------------- UPDATE ---------------- */
    @Override
    public StatConfig update(String page, String id, StatConfig stat) {
        PageConfig pageConfig = workspaceService.getPage(page);

        pageConfig.getStatsById().put(id, stat);
        return stat;
    }

    /* ---------------- DELETE ---------------- */
    @Override
    public void delete(String page, String id) {
        PageConfig pageConfig = workspaceService.getPage(page);

        pageConfig.getStatsById().remove(id);
        pageConfig.getStatsOrder().remove(id);
    }

    /* ---------------- UPDATE ORDER ---------------- */
    @Override
    public List<String> updateOrder(String page, List<String> statsOrder) {
        PageConfig pageConfig = workspaceService.getPage(page);

        pageConfig.setStatsOrder(statsOrder);
        return statsOrder;
    }

    @Override
    public void deleteByUserId() {
        WorkspaceEntity workspaceEntity = workspaceService.get();
        PageConfig dashboard = workspaceEntity.getData()
                                              .getPages()
                                              .get("dashboard");
        dashboard.setStatsById(new HashMap<>());
        dashboard.setStatsOrder(new ArrayList<>());

        workspaceService.save(workspaceEntity);
    }
}