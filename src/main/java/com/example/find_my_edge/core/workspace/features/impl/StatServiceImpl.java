package com.example.find_my_edge.core.workspace.features.impl;

import com.example.find_my_edge.core.workspace.dto.core.PageDTO;
import com.example.find_my_edge.core.workspace.dto.core.WorkspaceDTO;
import com.example.find_my_edge.core.workspace.dto.stat.StatDTO;
import com.example.find_my_edge.core.workspace.features.StatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final WorkspaceDTO workspace;

    /* ---------------- HELPER ---------------- */
    private PageDTO getOrCreatePage(String page) {
        if (workspace.getPages() == null) {
            workspace.setPages(new HashMap<>());
        }

        return workspace.getPages().computeIfAbsent(page, key -> new PageDTO());
    }

    /* ---------------- ADD ---------------- */
    @Override
    public StatDTO addStat(String page, StatDTO stat) {
        PageDTO pageDTO = getOrCreatePage(page);

        pageDTO.getStatsById().put(stat.getId(), stat);
        pageDTO.getStatsOrder().add(stat.getId());

        System.out.println(pageDTO);
        return stat;
    }

    /* ---------------- UPDATE ---------------- */
    @Override
    public StatDTO updateStat(String page, String id, StatDTO stat) {
        PageDTO pageDTO = getOrCreatePage(page);

        pageDTO.getStatsById().put(id, stat);
        return stat;
    }

    /* ---------------- GET ALL ---------------- */
    @Override
    public Map<String, Object> getAll(String page) {
        PageDTO pageDTO = getOrCreatePage(page);
        System.out.println(pageDTO);
        return Map.of("statsById", pageDTO.getStatsById(), "statsOrder", pageDTO.getStatsOrder());
    }

    /* ---------------- DELETE ---------------- */
    @Override
    public void delete(String page, String id) {
        PageDTO pageDTO = getOrCreatePage(page);

        pageDTO.getStatsById().remove(id);
        pageDTO.getStatsOrder().remove(id);
    }

    /* ---------------- UPDATE ORDER ---------------- */
    @Override
    public List<String> updateStatsOrder(String page, List<String> statsOrder) {
        PageDTO pageDTO = getOrCreatePage(page);

        pageDTO.setStatsOrder(statsOrder);
        return statsOrder;
    }
}