package com.example.find_my_edge.workspace.service.impl;

import com.example.find_my_edge.common.auth.AuthService;
import com.example.find_my_edge.workspace.config.page.PageConfig;
import com.example.find_my_edge.workspace.entity.WorkspaceEntity;
import com.example.find_my_edge.workspace.exception.PageNotFoundException;
import com.example.find_my_edge.workspace.exception.WorkspaceNotFoundException;
import com.example.find_my_edge.workspace.model.WorkspaceData;
import com.example.find_my_edge.workspace.repository.WorkspaceRepository;
import com.example.find_my_edge.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkspaceServiceImpl implements WorkspaceService {
    private final AuthService authService;
    private final WorkspaceRepository workspaceRepository;

    @Override
    public WorkspaceEntity get() {
        String currentUserId = authService.getCurrentUserId();
        return workspaceRepository.findByUserId(currentUserId)
                                  .orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found for user: " + currentUserId));
    }

    @Override
    public WorkspaceEntity save(WorkspaceEntity workspaceEntity) {
        return workspaceRepository.save(workspaceEntity);
    }

    @Override
    public WorkspaceEntity update(WorkspaceEntity workspaceEntity) {
        return workspaceRepository.save(workspaceEntity);
    }

    @Override
    public PageConfig getPage(String page) {
        WorkspaceEntity workspaceEntity = get();

        WorkspaceData data = workspaceEntity.getData();
        if (data == null || data.getPages() == null) {
            throw new WorkspaceNotFoundException("Workspace has no pages");
        }

        PageConfig pageConfig = data.getPages().get(page);
        if (pageConfig == null) {
            throw new PageNotFoundException("Page not found: " + page);
        }

        return pageConfig;
    }

}
