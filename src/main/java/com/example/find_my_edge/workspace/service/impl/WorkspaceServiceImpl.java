package com.example.find_my_edge.workspace.service.impl;

import com.example.find_my_edge.common.auth.service.CurrentUserService;
import com.example.find_my_edge.workspace.config.page.PageConfig;
import com.example.find_my_edge.workspace.entity.WorkspaceEntity;
import com.example.find_my_edge.workspace.enums.PageType;
import com.example.find_my_edge.workspace.exception.PageNotFoundException;
import com.example.find_my_edge.workspace.exception.WorkspaceNotFoundException;
import com.example.find_my_edge.workspace.model.WorkspaceData;
import com.example.find_my_edge.workspace.registry.WorkspaceRegistry;
import com.example.find_my_edge.workspace.repository.WorkspaceRepository;
import com.example.find_my_edge.workspace.service.WorkspaceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class WorkspaceServiceImpl implements WorkspaceService {
    private final CurrentUserService currentUserService;
    private final WorkspaceRepository workspaceRepository;

    private final WorkspaceRegistry workspaceRegistry;


    @Override
    public WorkspaceEntity get() {
        UUID userId = currentUserService.getUserId();

        return workspaceRepository
                .findByUserId(userId)
                .orElseThrow(() -> new WorkspaceNotFoundException(userId));
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
        UUID userId = currentUserService.getUserId();
        workspaceRepository.deleteByUserId(userId);
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

    @Override
    public void removeSchemaReferences(String schemaId) {
        getPageAndModify(
                page -> page.getColumnWidths().remove(schemaId),
                PageType.TRADE_METRIC.key()
        );
    }

    @Override
    public void removeTradeReferences(String tradeId) {
        getPageAndModify(
                page -> page.getHighlightedRows().remove(tradeId),
                PageType.TRADE_METRIC.key()
        );
    }

    @Override
    public void createDefaultWorkspace(UUID userId) {
        WorkspaceEntity workspaceEntity = new WorkspaceEntity();
        workspaceEntity.setUserId(userId);
        workspaceEntity.setData(workspaceRegistry.createDefaultWorkspaceData());
        save(workspaceEntity);
    }
}
