package com.example.find_my_edge.workspace.service;

import com.example.find_my_edge.workspace.config.page.PageConfig;
import com.example.find_my_edge.workspace.entity.WorkspaceEntity;

public interface WorkspaceService {
    WorkspaceEntity get();

    WorkspaceEntity save(WorkspaceEntity workspaceEntity);

    WorkspaceEntity update(WorkspaceEntity workspaceEntity);

    PageConfig getPage(String page);
}
