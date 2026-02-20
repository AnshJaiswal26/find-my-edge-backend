package com.example.find_my_edge.core.workspace.dto.chart;

import com.example.find_my_edge.common.dto.FilterDTO;
import com.example.find_my_edge.common.dto.GroupSpecDTO;
import com.example.find_my_edge.common.dto.RangeDTO;

import java.util.List;
import java.util.Map;

public class ChartDTO {
    private ChartMetaDTO meta;
    private Map<String, Object> layout;
    private SortDto sort;
    private GroupSpecDTO groupSpec;
    private List<FilterDTO> filters;
    private RangeDTO selection;
    private Map<String, Object> xSeriesConfig;
    private List<Map<String, Object>> ySeriesConfig;
}
