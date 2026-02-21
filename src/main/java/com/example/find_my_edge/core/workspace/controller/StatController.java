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
@RequestMapping("/workspace/{page}/stats")
@RequiredArgsConstructor
public class StatController extends BaseController {

    private final StatService statService;

    /* ---------------- CREATE ---------------- */
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> addStat(
            @PathVariable String page,
            @RequestBody StatDTO stat
    ) {
        StatDTO saved = statService.addStat(page, stat);
        return buildResponse(saved, "Stat created successfully");
    }

    /* ---------------- GET ALL ---------------- */
    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getAll(
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
            @RequestBody StatDTO stat
    ) {
        StatDTO updated = statService.updateStat(page, id, stat);
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
    public ResponseEntity<ApiResponse<Object>> updateOrder(
            @PathVariable String page,
            @RequestBody List<String> statsOrder
    ) {
        List<String> updatedOrder = statService.updateStatsOrder(page, statsOrder);
        return buildResponse(updatedOrder, "Stats order updated successfully");
    }

}