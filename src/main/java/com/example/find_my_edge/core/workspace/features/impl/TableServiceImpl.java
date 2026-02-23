package com.example.find_my_edge.core.workspace.features.impl;

import com.example.find_my_edge.core.workspace.dto.core.PageDTO;
import com.example.find_my_edge.core.workspace.features.TableService;
import com.example.find_my_edge.core.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TableServiceImpl implements TableService {
    private final WorkspaceService workspaceService;

    @Override
    public List<String> updateColumnOrder(Long workspaceId, String page, List<String> order) {
        PageDTO savedPage = workspaceService.getPage(workspaceId, page);
        savedPage.setColumnsOrder(order);

        return order;
    }

    @Override
    public Integer updateColumnWidth(Long workspaceId, String page, String id, Integer width) {
        PageDTO savedPage = workspaceService.getPage(workspaceId, page);
        savedPage.getColumnWidths().put(id, width);

        return width;
    }
}
