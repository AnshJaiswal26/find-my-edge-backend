package com.example.find_my_edge.core.workspace.features.impl;

import com.example.find_my_edge.core.workspace.dto.core.PageDTO;
import com.example.find_my_edge.core.workspace.features.PageService;
import com.example.find_my_edge.core.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class PageServiceImpl implements PageService {

    private final WorkspaceService workspaceService;

    @Override
    public PageDTO get(Long workspaceId, String page) {
       return  workspaceService.getPage(workspaceId, page);
    }

}
