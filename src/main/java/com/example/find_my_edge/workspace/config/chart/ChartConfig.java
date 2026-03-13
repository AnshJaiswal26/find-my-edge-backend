package com.example.find_my_edge.workspace.config.chart;

import com.example.find_my_edge.analytics.config.FilterConfig;
import com.example.find_my_edge.analytics.config.GroupConfig;
import com.example.find_my_edge.analytics.config.SortConfig;
import com.example.find_my_edge.workspace.enums.ChartCategory;
import com.example.find_my_edge.workspace.enums.ChartMode;
import com.example.find_my_edge.workspace.enums.ChartType;
import com.example.find_my_edge.workspace.enums.Source;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

@JsonAutoDetect(
        fieldVisibility = ANY,
        getterVisibility = NONE,
        isGetterVisibility = NONE
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartConfig {
    private String id;

    private ChartCategory category;

    private ChartType type;
    private ChartMode mode;
    private Source source;

    private XMetric xMetric;

    @JsonProperty("groupSpec")
    private GroupConfig group;

    private List<SeriesConfig> series;

    private Map<String, Object> layout;

    private SortConfig sort;
    private SelectionConfig selection;
    private List<FilterConfig> filters;
}