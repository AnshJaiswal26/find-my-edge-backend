package com.example.find_my_edge.workspace.features.impl;

import com.example.find_my_edge.workspace.config.page.PageConfig;
import com.example.find_my_edge.workspace.features.PageService;
import com.example.find_my_edge.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class PageServiceImpl implements PageService {

    private final WorkspaceService workspaceService;

    @Override
    public PageConfig get(String page) {
       return  workspaceService.getPage(page);
    }

}
