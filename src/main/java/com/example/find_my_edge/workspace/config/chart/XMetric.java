package com.example.find_my_edge.workspace.config.chart;

import com.example.find_my_edge.common.enums.SemanticType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class XMetric {
    private String field;
    private String label;
    private SemanticType type;
}
