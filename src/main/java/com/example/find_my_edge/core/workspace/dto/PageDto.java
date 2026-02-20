package com.example.find_my_edge.core.workspace.dto;

import com.example.find_my_edge.core.workspace.dto.chart.ChartDTO;
import com.example.find_my_edge.core.workspace.dto.chart.ChartLayoutDTO;

import java.util.Map;

public class PageDto {
    Map<String, ChartLayoutDTO> chartGridLayout;
    Map<String, ChartDTO> charts;



}
