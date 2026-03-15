package com.example.find_my_edge.workspace.features.impl;

import com.example.find_my_edge.workspace.config.page.PageGridLayoutConfig;
import com.example.find_my_edge.workspace.features.PageLayoutService;
import com.example.find_my_edge.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PageLayoutServiceImpl implements PageLayoutService {

    private final WorkspaceService workspaceService;

    @Override
    public Map<String, PageGridLayoutConfig> updateLayout(
            String pageName,
            Map<String, PageGridLayoutConfig> layout
    ) {

        workspaceService.getPageAndModify(
                page -> page.getGridLayout().putAll(layout),
                pageName
        );

        return layout;
    }
}