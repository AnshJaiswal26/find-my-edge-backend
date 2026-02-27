package com.example.find_my_edge.workspace.features.impl;

import com.example.find_my_edge.workspace.features.TableService;
import com.example.find_my_edge.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TableServiceImpl implements TableService {
    private final WorkspaceService workspaceService;

    @Override
    public Integer updateColumnWidth(String pageName, String id, Integer width) {
        workspaceService.getPageAndModify(
                page ->
                        page.getColumnWidths().put(id, width),
                pageName
        );
        return width;
    }
}
