package com.example.find_my_edge.core.workspace.service.impl;

import com.example.find_my_edge.common.enums.Page;
import com.example.find_my_edge.core.workspace.dto.core.PageDTO;
import com.example.find_my_edge.core.workspace.dto.stat.StatDTO;
import com.example.find_my_edge.core.workspace.entity.Workspace;
import com.example.find_my_edge.core.workspace.exception.PageNotFoundException;
import com.example.find_my_edge.core.workspace.exception.WorkspaceNotFoundException;
import com.example.find_my_edge.core.workspace.model.WorkspaceData;
import com.example.find_my_edge.core.workspace.repository.WorkspaceRepository;
import com.example.find_my_edge.core.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkspaceServiceImpl implements WorkspaceService {
    private final WorkspaceRepository workspaceRepository;

    @Override
    public Workspace get(Long id) {
        return workspaceRepository.findById(id)
                                  .orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found: " + id));
    }

    @Override
    public Workspace save(Workspace workspace) {
        return workspaceRepository.save(workspace);
    }

    @Override
    public Workspace update(Workspace workspace) {
        return workspaceRepository.save(workspace);
    }

    @Override
    public PageDTO getPage(Long workspaceId, String page) {
        Workspace workspace = get(workspaceId);

        WorkspaceData data = workspace.getData();
        if (data == null || data.getPages() == null) {
            throw new WorkspaceNotFoundException("Workspace has no pages");
        }

        PageDTO pageDTO = data.getPages().get(page);
        if (pageDTO == null) {
            throw new PageNotFoundException("Page not found: " + page);
        }

        return pageDTO;
    }

    @Override
    public void seedStats(Page page, StatDTO statDTO) {

    }
}
