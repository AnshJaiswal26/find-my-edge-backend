package com.example.find_my_edge.workspace.features.impl;

import com.example.find_my_edge.workspace.config.page.PageConfig;
import com.example.find_my_edge.workspace.features.TableService;
import com.example.find_my_edge.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TableServiceImpl implements TableService {
    private final WorkspaceService workspaceService;

    @Override
    public List<String> updateColumnOrder(String page, List<String> order) {
        PageConfig savedPage = workspaceService.getPage(page);
        savedPage.setColumnsOrder(order);

        return order;
    }

    @Override
    public Integer updateColumnWidth(String page, String id, Integer width) {
        PageConfig savedPage = workspaceService.getPage(page);
        savedPage.getColumnWidths().put(id, width);

        return width;
    }
}
