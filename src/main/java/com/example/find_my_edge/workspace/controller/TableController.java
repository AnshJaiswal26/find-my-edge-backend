package com.example.find_my_edge.workspace.controller;

import com.example.find_my_edge.common.controller.BaseController;
import com.example.find_my_edge.common.dto.ApiResponse;
import com.example.find_my_edge.workspace.features.TableService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/pages/{pageName}/table")
@RequiredArgsConstructor
public class TableController extends BaseController {

    private final TableService tableService;

    @PatchMapping("/columns/{columnId}/width")
    public ResponseEntity<ApiResponse<Integer>> updateColumnWidth(
            @PathVariable String pageName,
            @PathVariable String columnId,
            @RequestBody Integer width
    ) {
        tableService.updateColumnWidth(pageName, columnId, width);

        return buildResponse(width, "Column width updated successfully");
    }

    @PatchMapping("/rows/{rowId}/highlight")
    public ResponseEntity<ApiResponse<Boolean>> highlightRow(
            @PathVariable String pageName,
            @PathVariable String rowId,
            @RequestBody Boolean highlight
    ) {
        tableService.updateHighLightedRow(pageName, rowId, highlight);

        return buildResponse(highlight, "Highlight row updated successfully");
    }
}