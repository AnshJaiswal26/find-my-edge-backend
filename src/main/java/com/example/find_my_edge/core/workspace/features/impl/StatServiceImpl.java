package com.example.find_my_edge.core.workspace.features.impl;

import com.example.find_my_edge.core.workspace.dto.core.PageDTO;
import com.example.find_my_edge.core.workspace.entity.Workspace;
import com.example.find_my_edge.core.workspace.dto.stat.StatDTO;
import com.example.find_my_edge.core.workspace.features.StatService;
import com.example.find_my_edge.core.workspace.service.WorkspaceService;
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
    public Map<String, Object> getAll(Long workspaceId, String page) {
        PageDTO pageDTO = workspaceService.getPage(workspaceId, page);
        System.out.println(pageDTO);
        return Map.of(
                "statsById", pageDTO.getStatsById(),
                "statsOrder", pageDTO.getStatsOrder()
        );
    }

    /* ---------------- GET BY id ---------------- */
    @Override
    public StatDTO getById(Long workspaceId, String page, String id) {
        PageDTO pageDTO = workspaceService.getPage(workspaceId, page);
        return pageDTO.getStatsById().get(id);
    }

    /* ---------------- ADD ---------------- */
    @Override
    public StatDTO create(Long workspaceId, String page, StatDTO stat) {
        PageDTO pageDTO = workspaceService.getPage(workspaceId, page);

        pageDTO.getStatsById().put(stat.getId(), stat);
        pageDTO.getStatsOrder().add(stat.getId());

        System.out.println(pageDTO);
        return stat;
    }

    /* ---------------- UPDATE ---------------- */
    @Override
    public StatDTO update(Long workspaceId, String page, String id, StatDTO stat) {
        PageDTO pageDTO = workspaceService.getPage(workspaceId, page);

        pageDTO.getStatsById().put(id, stat);
        return stat;
    }

    /* ---------------- DELETE ---------------- */
    @Override
    public void delete(Long workspaceId, String page, String id) {
        PageDTO pageDTO = workspaceService.getPage(workspaceId, page);

        pageDTO.getStatsById().remove(id);
        pageDTO.getStatsOrder().remove(id);
    }

    /* ---------------- UPDATE ORDER ---------------- */
    @Override
    public List<String> updateOrder(Long workspaceId, String page, List<String> statsOrder) {
        PageDTO pageDTO = workspaceService.getPage(workspaceId, page);

        pageDTO.setStatsOrder(statsOrder);
        return statsOrder;
    }
}