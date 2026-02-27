package com.example.find_my_edge.api.workspace.controller;

import com.example.find_my_edge.common.controller.BaseController;
import com.example.find_my_edge.common.response.ApiResponse;
import com.example.find_my_edge.workspace.config.chart.ChartConfig;
import com.example.find_my_edge.workspace.config.chart.SeriesConfig;
import com.example.find_my_edge.workspace.features.ChartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pages/{pageName}/charts")
@RequiredArgsConstructor
public class ChartController extends BaseController {

    private final ChartService chartService;

    /* ---------------- CREATE ---------------- */
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> create(
            @PathVariable String pageName,
            @RequestBody ChartConfig dto
    ) {
        ChartConfig created = chartService.create(pageName, dto);
        return buildResponse(created, "Chart created successfully");
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
    public ResponseEntity<ApiResponse<Object>> delete(
            @PathVariable String pageName,
            @PathVariable String chartId
    ) {
        chartService.delete(pageName, chartId);
        return buildResponse(null, "Chart deleted successfully");
    }

    /* ---------------- UPDATE LAYOUT ---------------- */
    @PatchMapping("/{chartId}/layout")
    public ResponseEntity<ApiResponse<Object>> updateLayout(
            @PathVariable String pageName,
            @PathVariable String chartId,
            @RequestBody Map<String, Object> layout
    ) {
        Map<String, Object> updatedLayout =
                chartService.updateLayout(pageName, chartId, layout);

        return buildResponse(updatedLayout, "Chart layout updated successfully");
    }

    /* ---------------- UPDATE SERIES ---------------- */
    @PatchMapping("/{chartId}/series")
    public ResponseEntity<ApiResponse<Object>> updateSeries(
            @PathVariable String pageName,
            @PathVariable String chartId,
            @RequestBody List<SeriesConfig> seriesConfig
    ) {
        List<SeriesConfig> updatedSeries =
                chartService.updateSeriesConfig(pageName, chartId, seriesConfig);

        return buildResponse(updatedSeries, "Chart series updated successfully");
    }
}