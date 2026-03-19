package com.example.find_my_edge.workspace.controller;

import com.example.find_my_edge.common.controller.BaseController;
import com.example.find_my_edge.common.dto.ApiResponse;
import com.example.find_my_edge.workspace.config.chart.ChartConfig;
import com.example.find_my_edge.workspace.dto.ChartLayoutDto;
import com.example.find_my_edge.workspace.dto.ChartRequest;
import com.example.find_my_edge.workspace.dto.ChartResponse;
import com.example.find_my_edge.workspace.features.ChartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pages/{pageName}/charts")
@RequiredArgsConstructor
public class ChartController extends BaseController {

    private final ChartService chartService;

    /* ---------------- CREATE ---------------- */
    @PostMapping
    public ResponseEntity<ApiResponse<ChartResponse>> create(
            @PathVariable String pageName,
            @RequestBody ChartRequest dto
    ) {
        ChartResponse created = chartService.create(pageName, dto);
        return buildResponse(created, "Chart created successfully", HttpStatus.CREATED);
    }

    /* ---------------- GET BY ID ---------------- */
    @GetMapping("/{chartId}")
    public ResponseEntity<ApiResponse<Object>> getById(
            @PathVariable String pageName,
            @PathVariable String chartId
    ) {
        ChartConfig chart = chartService.getById(pageName, chartId);
        return buildResponse(chart, "Chart fetched successfully");
    }

    /* ---------------- GET ALL ---------------- */
    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getAll(
            @PathVariable String pageName
    ) {
        Map<String, ChartConfig> charts = chartService.getAll(pageName);
        return buildResponse(charts, "Charts fetched successfully");
    }

    /* ---------------- UPDATE ---------------- */
    @PutMapping("/{chartId}")
    public ResponseEntity<ApiResponse<Object>> update(
            @PathVariable String pageName,
            @PathVariable String chartId,
            @RequestBody ChartConfig dto
    ) {
        ChartConfig updated = chartService.update(pageName, chartId, dto);
        return buildResponse(updated, "Chart updated successfully");
    }

    /* ---------------- DELETE ---------------- */
    @DeleteMapping("/{chartId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable String pageName,
            @PathVariable String chartId
    ) {
        chartService.delete(pageName, chartId);
        return buildNoContentResponse("Chart deleted successfully");
    }

    /* ---------------- UPDATE LAYOUT ---------------- */
    @PatchMapping("/{chartId}/layout")
    public ResponseEntity<ApiResponse<Object>> updateLayout(
            @PathVariable String pageName,
            @PathVariable String chartId,
            @RequestBody ChartLayoutDto dto
    ) {
        ChartLayoutDto updatedLayout =
                chartService.updateLayout(pageName, chartId, dto);

        return buildResponse(updatedLayout, "Chart layout updated successfully");
    }
}