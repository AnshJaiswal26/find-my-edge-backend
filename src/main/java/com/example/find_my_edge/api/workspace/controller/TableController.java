package com.example.find_my_edge.api.workspace.controller;

import com.example.find_my_edge.workspace.features.TableService;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/workspace/{page}")
@RequiredArgsConstructor
public class TableController {

    private final TableService tableService;

    @PostMapping("/columnOrder")
    public List<String> updateColumnOrder(
            @PathVariable String page, List<String> columnsOrder
    ) {
        return tableService.updateColumnOrder(page, columnsOrder);
    }

    @PostMapping("/columnWidth/{columnId}")
    public int updateColumnWidth(
            @PathVariable String columnId, @PathVariable String page, int width
    ) {
        return tableService.updateColumnWidth(page, columnId, width);
    }
}