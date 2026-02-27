package com.example.find_my_edge.workspace.service;

import com.example.find_my_edge.workspace.config.page.PageConfig;
import com.example.find_my_edge.workspace.entity.WorkspaceEntity;

import java.util.Map;
import java.util.function.Consumer;

public interface WorkspaceService {
    WorkspaceEntity get();

    WorkspaceEntity getAndModify(Consumer<Map<String, PageConfig>> updater);

    void getPageAndModify(Consumer<PageConfig> updater, String page);

    WorkspaceEntity save(WorkspaceEntity workspaceEntity);

    WorkspaceEntity update(WorkspaceEntity workspaceEntity);

    PageConfig getPage(String page);

    void delete();
}
