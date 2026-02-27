package com.example.find_my_edge.api.workspace.controller;

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
            @PathVariable String columnId, @PathVariable String pageName, int width
    ) {
        return buildResponse(
                tableService.updateColumnWidth(pageName, columnId, width),
                "Column width updated successfully"
        );
    }
}