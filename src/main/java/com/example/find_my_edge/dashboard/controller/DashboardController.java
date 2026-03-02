package com.example.find_my_edge.dashboard.controller;

import com.example.find_my_edge.dashboard.model.DashboardData;
import com.example.find_my_edge.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard/init")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardData> getDashboard() {

        DashboardData response = dashboardService.init();

        return ResponseEntity.ok(response);
    }
}