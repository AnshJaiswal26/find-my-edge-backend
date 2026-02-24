package com.example.find_my_edge.workspace.registry;

import com.example.find_my_edge.workspace.config.chart.ChartConfig;
import com.example.find_my_edge.workspace.config.chart.ChartMetaConfig;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class ChartRegistry {

    private final LayoutRegistry layoutRegistry;

    private Map<String, ChartConfig> charts;
    private Set<String> chartOrder;

    @PostConstruct
    public void init() {
        Map<String, ChartConfig> tempMap = new HashMap<>();
        Set<String> tempOrder = new LinkedHashSet<>();

        buildDefaultCharts().forEach(chart -> {
            tempMap.put(chart.getMeta().getId(), chart);
            tempOrder.add(chart.getMeta().getId());
        });

        this.charts = Map.copyOf(tempMap);
        this.chartOrder = Set.copyOf(tempOrder);
    }

    public List<ChartConfig> getAll() {
        return chartOrder.stream()
                         .map(charts::get)
                         .toList();
    }

    private List<ChartConfig> buildDefaultCharts() {
        return List.of(
                barChart(),
                lineChart(),
                pieChart(),
                radialBarChart()
        );
    }

    private ChartConfig barChart() {
        ChartConfig config = new ChartConfig();

        config.setMeta(ChartMetaConfig.builder()
                                      .id("chart-1")
                                      .type("bar")
                                      .category("series")
                                      .title("Bar Chart")
                                      .build());

        Map<String, Object> layout = layoutRegistry.get("bar");

        config.setLayout(layout);

        return config;
    }

    private ChartConfig lineChart() {
        ChartConfig config = new ChartConfig();

        config.setMeta(ChartMetaConfig.builder()
                                      .id("chart-2")
                                      .type("line")
                                      .category("series")
                                      .title("Line Chart")
                                      .build());

        Map<String, Object> layout = layoutRegistry.get("line");

        config.setLayout(layout);

        return config;
    }


    private ChartConfig pieChart() {
        ChartConfig config = new ChartConfig();

        config.setMeta(ChartMetaConfig.builder()
                                      .id("chart-1")
                                      .type("donut")
                                      .category("group")
                                      .title("Pie Chart")
                                      .build());

        Map<String, Object> layout = layoutRegistry.get("pie");

        config.setLayout(layout);

        return config;
    }

    private ChartConfig radialBarChart() {
        ChartConfig config = new ChartConfig();

        config.setMeta(ChartMetaConfig.builder()
                                      .id("chart-1")
                                      .type("donut")
                                      .category("group")
                                      .title("Pie Chart")
                                      .build());

        Map<String, Object> layout = layoutRegistry.get("pie");

        config.setLayout(layout);

        return config;
    }
}