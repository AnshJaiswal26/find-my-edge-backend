package com.example.find_my_edge.core.workspace.service.impl;

import com.example.find_my_edge.core.workspace.dto.core.PageDTO;
import com.example.find_my_edge.core.workspace.dto.core.WorkspaceDTO;
import com.example.find_my_edge.core.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkspaceServiceImpl implements WorkspaceService {
    private final WorkspaceDTO workspace;

    @Override
    public PageDTO getPage(String page) {
        return workspace.getPages().get(page);
    }

    @Override
    public PageDTO savePage(String page, PageDTO dto) {
        return workspace.getPages().put(page, dto);
    }

    @Override
    public PageDTO updatePage(String page, PageDTO dto) {
        return workspace.getPages().put(page, dto);
    }

    @Override
    public void deletePage(String page) {
        workspace.getPages().remove(page);
    }
}
