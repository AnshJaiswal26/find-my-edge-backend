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
@RequestMapping("/workspace/{page}/stats")
@RequiredArgsConstructor
public class StatController extends BaseController {

    private final StatService statService;

    /* ---------------- CREATE ---------------- */
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createStat(
            @PathVariable String page,
            @RequestBody StatConfig stat
    ) {
        StatConfig saved = statService.create(page, stat);
        return buildResponse(saved, "Stat created successfully");
    }

    /* ---------------- GET ALL ---------------- */
    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getAllStats(
            @PathVariable String page
    ) {
        Object stats = statService.getAll(page);
        return buildResponse(stats, "Stats fetched successfully");
    }

    /* ---------------- UPDATE ---------------- */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> updateStat(
            @PathVariable String page,
            @PathVariable String id,
            @RequestBody StatConfig stat
    ) {
        StatConfig updated = statService.update(page, id, stat);
        return buildResponse(updated, "Stat updated successfully");
    }

    /* ---------------- DELETE ---------------- */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteStat(
            @PathVariable String page,
            @PathVariable String id
    ) {
        statService.delete(page, id);
        return buildResponse(null, "Stat deleted successfully");
    }

    /* ---------------- UPDATE ORDER ---------------- */
    @PutMapping("/order")
    public ResponseEntity<ApiResponse<Object>> updateStatsOrder(
            @PathVariable String page,
            @RequestBody List<String> statsOrder
    ) {
        List<String> updatedOrder = statService.updateOrder(page, statsOrder);
        return buildResponse(updatedOrder, "Stats order updated successfully");
    }
}