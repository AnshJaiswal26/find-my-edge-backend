package com.example.find_my_edge.analytics.model;

import com.example.find_my_edge.analytics.engine.group.model.Group;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class ChartResult {

    private List<Group> groups;
    private Map<String, Map<String, Double>> series;
}