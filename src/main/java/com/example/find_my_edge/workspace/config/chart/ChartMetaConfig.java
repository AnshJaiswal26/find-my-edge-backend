package com.example.find_my_edge.workspace.config.chart;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChartMetaConfig {
    private String id;
    private String category;
    private String type;
    private String title;
}
