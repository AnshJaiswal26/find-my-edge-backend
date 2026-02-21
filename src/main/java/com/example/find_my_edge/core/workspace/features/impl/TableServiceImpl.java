package com.example.find_my_edge.core.workspace.features.impl;

import com.example.find_my_edge.core.workspace.dto.core.PageDTO;
import com.example.find_my_edge.core.workspace.dto.core.WorkspaceDTO;
import com.example.find_my_edge.core.workspace.features.TableService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TableServiceImpl implements TableService {
    private final WorkspaceDTO workspace;

    @Override
    public List<String> updateColumnOrder(String page, List<String> order) {
        workspace.getPages()
                 .computeIfAbsent(page, key -> new PageDTO())
                 .setColumnsOrder(order);
        return order;
    }

    @Override
    public Integer updateColumnWidth(String page, String id, Integer width) {
        return workspace.getPages()
                        .computeIfAbsent(page, key -> new PageDTO())
                        .getColumnWidths().put(id, width);
    }
}
