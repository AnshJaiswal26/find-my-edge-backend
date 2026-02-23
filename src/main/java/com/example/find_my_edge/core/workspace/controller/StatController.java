package com.example.find_my_edge.core.workspace.controller;

import com.example.find_my_edge.common.controller.BaseController;
import com.example.find_my_edge.common.response.ApiResponse;
import com.example.find_my_edge.core.workspace.dto.stat.StatDTO;
import com.example.find_my_edge.core.workspace.features.StatService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workspace/{workspaceId}/{page}/stats")
@RequiredArgsConstructor
public class StatController extends BaseController {

    private final StatService statService;

    /* ---------------- CREATE ---------------- */
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createStat(
            @PathVariable Long workspaceId,
            @PathVariable String page,
            @RequestBody StatDTO stat
    ) {
        StatDTO saved = statService.create(workspaceId, page, stat);
        return buildResponse(saved, "Stat created successfully");
    }

    /* ---------------- GET ALL ---------------- */
    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getAllStats(
            @PathVariable Long workspaceId,
            @PathVariable String page
    ) {
        Object stats = statService.getAll(workspaceId, page);
        return buildResponse(stats, "Stats fetched successfully");
    }

    /* ---------------- UPDATE ---------------- */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> updateStat(
            @PathVariable Long workspaceId,
            @PathVariable String page,
            @PathVariable String id,
            @RequestBody StatDTO stat
    ) {
        StatDTO updated = statService.update(workspaceId, page, id, stat);
        return buildResponse(updated, "Stat updated successfully");
    }

    /* ---------------- DELETE ---------------- */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteStat(
            @PathVariable Long workspaceId,
            @PathVariable String page,
            @PathVariable String id
    ) {
        statService.delete(workspaceId, page, id);
        return buildResponse(null, "Stat deleted successfully");
    }

    /* ---------------- UPDATE ORDER ---------------- */
    @PutMapping("/order")
    public ResponseEntity<ApiResponse<Object>> updateStatsOrder(
            @PathVariable Long workspaceId,
            @PathVariable String page,
            @RequestBody List<String> statsOrder
    ) {
        List<String> updatedOrder = statService.updateOrder(workspaceId, page, statsOrder);
        return buildResponse(updatedOrder, "Stats order updated successfully");
    }
}