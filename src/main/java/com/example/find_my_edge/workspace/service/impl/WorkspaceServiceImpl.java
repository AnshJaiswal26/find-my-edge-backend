package com.example.find_my_edge.workspace.service.impl;

import com.example.find_my_edge.common.auth.AuthService;
import com.example.find_my_edge.workspace.config.page.PageConfig;
import com.example.find_my_edge.workspace.entity.WorkspaceEntity;
import com.example.find_my_edge.workspace.exception.PageNotFoundException;
import com.example.find_my_edge.workspace.exception.WorkspaceNotFoundException;
import com.example.find_my_edge.workspace.model.WorkspaceData;
import com.example.find_my_edge.workspace.repository.WorkspaceRepository;
import com.example.find_my_edge.workspace.service.WorkspaceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class WorkspaceServiceImpl implements WorkspaceService {
    private final AuthService authService;
    private final WorkspaceRepository workspaceRepository;

    @Override  // for dev
    public WorkspaceEntity get() {
        String userId = authService.getCurrentUserId();

        return workspaceRepository.findByUserId(userId)
                                  .orElseGet(() -> createDefaultWorkspace(userId));
    }

    private WorkspaceEntity createDefaultWorkspace(String userId) {
        WorkspaceEntity workspace = new WorkspaceEntity();
        workspace.setUserId(userId);

        // optional: set default data
        workspace.setData(new WorkspaceData());

        return workspaceRepository.save(workspace);
    }

    @Override
    public WorkspaceEntity getAndModify(Consumer<Map<String, PageConfig>> updater) {

        WorkspaceEntity workspace = get();

        WorkspaceData data = ensureData(workspace);

        Map<String, PageConfig> pages = ensurePages(data);

        updater.accept(pages);

        return workspaceRepository.save(workspace);
    }

    @Override
    public void getPageAndModify(Consumer<PageConfig> updater, String page) {

        WorkspaceEntity workspace = get();

        WorkspaceData data = ensureData(workspace);

        Map<String, PageConfig> pages = ensurePages(data);

        PageConfig pageConfig = pages.get(page);

        if (pageConfig == null) {
            throw new PageNotFoundException(page);
        }

        updater.accept(pageConfig);

        workspaceRepository.save(workspace);

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
            throw new WorkspaceNotFoundException();
        }

        PageConfig pageConfig = data.getPages().get(page);
        if (pageConfig == null) {
            throw new PageNotFoundException(page);
        }

        return pageConfig;
    }

    @Override
    @Transactional
    public void delete() {
        String currentUserId = authService.getCurrentUserId();
        workspaceRepository.deleteByUserId(currentUserId);
    }

    private WorkspaceData ensureData(WorkspaceEntity workspace) {

        if (workspace.getData() == null) {
            workspace.setData(new WorkspaceData());
        }

        return workspace.getData();
    }

    private Map<String, PageConfig> ensurePages(WorkspaceData data) {

        if (data.getPages() == null) {
            data.setPages(new HashMap<>());
        }

        return data.getPages();
    }
}
