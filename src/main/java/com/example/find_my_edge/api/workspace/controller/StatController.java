package com.example.find_my_edge.api.workspace.controller;

import com.example.find_my_edge.common.controller.BaseController;
import com.example.find_my_edge.common.response.ApiResponse;
import com.example.find_my_edge.workspace.config.stat.StatConfig;
import com.example.find_my_edge.workspace.features.StatService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/pages/{pageName}/stats")
@RequiredArgsConstructor
public class StatController extends BaseController {

    private final StatService statService;

    /* ---------------- CREATE ---------------- */
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createStat(
            @PathVariable String pageName,
            @RequestBody StatConfig stat
    ) {
        StatConfig saved = statService.create(pageName, stat);
        return buildResponse(saved, "Stat created successfully");
    }

    /* ---------------- GET ALL ---------------- */
    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getAllStats(
            @PathVariable String pageName
    ) {
        Object stats = statService.getAll(pageName);
        return buildResponse(stats, "Stats fetched successfully");
    }

    /* ---------------- UPDATE ---------------- */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> updateStat(
            @PathVariable String pageName,
            @PathVariable String id,
            @RequestBody StatConfig stat
    ) {
        StatConfig updated = statService.update(pageName, id, stat);
        return buildResponse(updated, "Stat updated successfully");
    }

    /* ---------------- DELETE ---------------- */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteStat(
            @PathVariable String pageName,
            @PathVariable String id
    ) {
        statService.delete(pageName, id);
        return buildResponse(null, "Stat deleted successfully");
    }

    /* ---------------- UPDATE ORDER ---------------- */
    @PutMapping("/order")
    public ResponseEntity<ApiResponse<Object>> updateStatsOrder(
            @PathVariable String pageName,
            @RequestBody List<String> statsOrder
    ) {
        List<String> updatedOrder = statService.updateOrder(pageName, statsOrder);
        return buildResponse(updatedOrder, "Stats order updated successfully");
    }
}