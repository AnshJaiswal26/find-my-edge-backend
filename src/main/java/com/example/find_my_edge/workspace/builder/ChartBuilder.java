package com.example.find_my_edge.workspace.builder;

import com.example.find_my_edge.analytics.config.SortConfig;
import com.example.find_my_edge.workspace.config.chart.ChartConfig;
import com.example.find_my_edge.workspace.config.chart.SelectionConfig;
import com.example.find_my_edge.workspace.config.chart.SeriesConfig;
import com.example.find_my_edge.workspace.config.chart.XMetric;
import com.example.find_my_edge.workspace.enums.ChartCategory;
import com.example.find_my_edge.workspace.enums.ChartMode;
import com.example.find_my_edge.workspace.enums.ChartType;
import com.example.find_my_edge.workspace.enums.Source;
import com.example.find_my_edge.workspace.registry.LayoutRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class ChartBuilder {

    private final LayoutRegistry layoutRegistry;

    public ChartConfig buildChart(
            ChartType type,
            Map<String, Object> layoutOverrides,
            XMetric xMetric,
            List<SeriesConfig> seriesConfigs
    ) {

        String layoutKey = getLayoutKey(type);

        Map<String, Object> layout = new HashMap<>(layoutRegistry.get(layoutKey));
        layout.putAll(layoutOverrides);

        return ChartConfig.builder()
                          .id(UUID.randomUUID().toString())
                          .type(type)
                          .category(resolveCategory(type))
                          .mode(resolveMode(type))
                          .source(Source.USER)
                          .layout(layout)
                          .xMetric(requiresXMetric(type) ? xMetric : null)
                          .series(seriesConfigs)
                          .sort(new SortConfig(null, "none"))
                          .filters(new ArrayList<>())
                          .selection(new SelectionConfig(null, null))
                          .build();
    }

    private String getLayoutKey(ChartType type) {
        return switch (type) {
            case BAR -> "bar";
            case LINE -> "line";
            case DONUT -> "pie";
            case RADIAL_BAR -> "radialBar";
            case RADAR -> "radar";
            case POLAR_AREA -> "polarArea";
        };
    }

    private ChartCategory resolveCategory(ChartType type) {
        return switch (type) {
            case BAR, LINE -> ChartCategory.SERIES;
            default -> ChartCategory.GROUP;
        };
    }

    private ChartMode resolveMode(ChartType type) {
        return switch (type) {
            case BAR, LINE -> ChartMode.SERIES;
            default -> null;
        };
    }

    private boolean requiresXMetric(ChartType type) {
        return switch (type) {
            case BAR, LINE -> true;
            default -> false;
        };
    }
}