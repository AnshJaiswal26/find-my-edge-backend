package com.example.find_my_edge.core.workspace.dto.core;

import com.example.find_my_edge.core.workspace.dto.chart.ChartDTO;
import com.example.find_my_edge.core.workspace.dto.chart.ChartLayoutDTO;
import com.example.find_my_edge.core.workspace.dto.stat.StatDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class PageDTO {
    // Charts
    private Map<String, ChartLayoutDTO> chartGridLayout = new HashMap<>();
    private Map<String, ChartDTO> charts = new HashMap<>();
    private List<String> chartOrder = new ArrayList<>();

    // Stats
    private List<String> statsOrder = new ArrayList<>();
    private Map<String, StatDTO> statsById = new HashMap<>();

    // Table
    private List<String> columnsOrder = new ArrayList<>();
    private Map<String, Integer> columnWidths = new HashMap<>();

}
