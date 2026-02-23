package com.example.find_my_edge.core.workspace.controller;

import com.example.find_my_edge.core.workspace.features.TableService;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/workspace/{workspaceId}/{page}")
@RequiredArgsConstructor
public class TableController {

    private final TableService tableService;

    @PostMapping("/columnOrder")
    public List<String> updateColumnOrder(
            @PathVariable Long workspaceId, @PathVariable String page, List<String> columnsOrder
    ) {
        return tableService.updateColumnOrder(workspaceId, page, columnsOrder);
    }

    @PostMapping("/columnWidth/{columnId}")
    public int updateColumnWidth(
            @PathVariable Long workspaceId, @PathVariable String columnId, @PathVariable String page, int width
    ) {
        return tableService.updateColumnWidth(workspaceId, page, columnId, width);
    }
}