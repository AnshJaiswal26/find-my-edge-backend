package com.example.find_my_edge.workspace.features.impl;

import com.example.find_my_edge.workspace.config.page.PageConfig;
import com.example.find_my_edge.workspace.config.stat.StatConfig;
import com.example.find_my_edge.workspace.features.StatService;
import com.example.find_my_edge.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public StatConfig create(String pageName, StatConfig stat) {

        workspaceService.getPageAndModify(
                page -> {
                    page.getStatsById().put(stat.getId(), stat);
                    page.getStatsOrder().add(stat.getId());
                },
                pageName
        );

        return stat;
    }

    /* ---------------- UPDATE ---------------- */
    @Override
    public StatConfig update(String pageName, String id, StatConfig stat) {

        workspaceService.getPageAndModify(
                page ->
                        page.getStatsById().put(id, stat),
                pageName
        );
        return stat;
    }

    /* ---------------- DELETE ---------------- */
    @Override
    public void delete(String pageName, String id) {

        workspaceService.getPageAndModify(
                page -> {
                    page.getStatsById().remove(id);
                    page.getStatsOrder().remove(id);
                },
                pageName
        );
    }

    /* ---------------- UPDATE ORDER ---------------- */
    @Override
    public List<String> updateOrder(String pageName, List<String> statsOrder) {

        workspaceService.getPageAndModify(
                page ->
                        page.setStatsOrder(statsOrder),
                pageName
        );

        return statsOrder;
    }

}