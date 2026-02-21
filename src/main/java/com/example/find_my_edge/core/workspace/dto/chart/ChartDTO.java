package com.example.find_my_edge.core.workspace.dto.chart;

import com.example.find_my_edge.common.dto.FilterDTO;
import com.example.find_my_edge.common.dto.GroupSpecDTO;
import com.example.find_my_edge.common.dto.RangeDTO;
import com.example.find_my_edge.common.dto.SortDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ChartDTO {
    private ChartMetaDTO meta;
    private Map<String, Object> layout = new HashMap<>();
    private SortDTO sort;
    private GroupSpecDTO groupSpec;
    private List<FilterDTO> filters = new ArrayList<>();
    private RangeDTO selection;
    private SeriesConfigDTO xSeriesConfig;
    private List<SeriesConfigDTO> ySeriesConfig = new ArrayList<>();
    private List<SeriesConfigDTO> seriesConfig = new ArrayList<>();
}
