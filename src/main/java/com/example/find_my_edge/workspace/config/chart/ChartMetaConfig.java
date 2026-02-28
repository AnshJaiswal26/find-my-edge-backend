package com.example.find_my_edge.workspace.config.chart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChartMetaConfig {
    private String id;
    private String category;
    private String type;
    private String title;
}
