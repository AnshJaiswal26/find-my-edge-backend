package com.example.find_my_edge.core.workspace.features;

import com.example.find_my_edge.core.workspace.dto.stat.StatDTO;

import java.util.List;
import java.util.Map;

public interface StatService {

    Map<String, Object> getAll(Long workspaceId, String page);

    StatDTO getById(Long workspaceId, String page, String id);

    StatDTO create(Long workspaceId, String page, StatDTO stat);

    StatDTO update(Long workspaceId, String page, String id, StatDTO stat);

    void delete(Long workspaceId, String page, String id);

    List<String> updateOrder(Long workspaceId, String page, List<String> order);

}
