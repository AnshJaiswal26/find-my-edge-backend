package com.example.find_my_edge.core.workspace.service;

import com.example.find_my_edge.common.enums.Page;
import com.example.find_my_edge.core.workspace.dto.core.PageDTO;
import com.example.find_my_edge.core.workspace.dto.stat.StatDTO;
import com.example.find_my_edge.core.workspace.entity.Workspace;

public interface WorkspaceService {
    Workspace get(Long id);

    Workspace save(Workspace workspace);

    Workspace update(Workspace workspace);

    PageDTO getPage(Long workspaceId, String page);

    void seedStats(Page page, StatDTO statDTO);
}
