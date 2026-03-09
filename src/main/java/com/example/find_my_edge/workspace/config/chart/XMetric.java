package com.example.find_my_edge.workspace.config.chart;

import com.example.find_my_edge.common.enums.SemanticType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class XMetric {
    private String field;
    private String label;
    private SemanticType type;
}
