package com.example.find_my_edge.workspace.controller;

import com.example.find_my_edge.common.controller.BaseController;
import com.example.find_my_edge.common.dto.ApiResponse;
import com.example.find_my_edge.workspace.config.page.PageGridLayoutConfig;
import com.example.find_my_edge.workspace.features.PageLayoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pages/{pageName}/layout")
@RequiredArgsConstructor
public class PageLayoutController extends BaseController {

    private final PageLayoutService pageLayoutService;

    @PatchMapping
    public ResponseEntity<ApiResponse<Map<String, PageGridLayoutConfig>>> updateLayout(
            @PathVariable String pageName,
            @RequestBody Map<String, PageGridLayoutConfig> layout
    ) {

        Map<String, PageGridLayoutConfig> updated =
                pageLayoutService.updateLayout(pageName, layout);

        return buildResponse(updated, "Grid layout updated successfully");
    }
}