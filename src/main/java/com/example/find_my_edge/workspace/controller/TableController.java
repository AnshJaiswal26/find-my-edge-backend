package com.example.find_my_edge.workspace.controller;

import com.example.find_my_edge.common.controller.BaseController;
import com.example.find_my_edge.common.response.ApiResponse;
import com.example.find_my_edge.workspace.features.TableService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/pages/{pageName}")
@RequiredArgsConstructor
public class TableController extends BaseController {

    private final TableService tableService;

    @PostMapping("table/columnWidth/{columnId}")
    public ResponseEntity<ApiResponse<Integer>> updateColumnWidth(
            @PathVariable String columnId, @PathVariable String pageName, @RequestBody Integer width
    ) {
        System.out.println(columnId + pageName + width);
        tableService.updateColumnWidth(pageName, columnId, width);
        return buildResponse(
                null,
                "Column width updated successfully"
        );
    }

    @PostMapping("table/highlightRow/{rowId}")
    public ResponseEntity<ApiResponse<Integer>> highlightRow(
            @PathVariable String rowId, @PathVariable String pageName, @RequestBody Boolean highlight
    ) {
        tableService.updateHighLightedRow(pageName, rowId, highlight);
        return buildResponse(
                null,
                "Highlight row updated successfully"
        );
    }
}