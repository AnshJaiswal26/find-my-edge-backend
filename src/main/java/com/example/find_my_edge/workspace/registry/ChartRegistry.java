package com.example.find_my_edge.workspace.registry;

import com.example.find_my_edge.analytics.config.SortConfig;
import com.example.find_my_edge.common.config.uiconfigs.ColorRuleConfig;
import com.example.find_my_edge.common.enums.SemanticType;
import com.example.find_my_edge.workspace.config.chart.*;
import com.example.find_my_edge.workspace.enums.ChartCategory;
import com.example.find_my_edge.workspace.enums.ChartMode;
import com.example.find_my_edge.workspace.enums.ChartType;
import com.example.find_my_edge.workspace.enums.Source;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.example.find_my_edge.common.builder.AstConfigBuilder.function;

@Component
@RequiredArgsConstructor
public class ChartRegistry {

    private final LayoutRegistry layoutRegistry;

    @Getter
    private Map<String, ChartConfig> charts;
    private Set<String> chartOrder;

    @PostConstruct
    public void init() {
        Map<String, ChartConfig> tempMap = new HashMap<>();
        Set<String> tempOrder = new LinkedHashSet<>();

        buildDefaultCharts().forEach(chart -> {
            tempMap.put(chart.getId(), chart);
            tempOrder.add(chart.getId());
        });

        this.charts = Map.copyOf(tempMap);
        this.chartOrder = Set.copyOf(tempOrder);
    }

    public List<ChartConfig> getAll() {
        return chartOrder.stream()
                         .map(charts::get)
                         .toList();
    }

    public List<String> getOrder() {
        return chartOrder.stream().toList();
    }

    public boolean has(String statId) {
        return chartOrder.contains(statId);
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
        Map<String, Object> layout = layoutRegistry.get("bar");
        layout.put("xTitleText", "Trades");
        layout.put("xFormat", "YYYY-MM-DD");
        layout.put("yTitleText", "Risk/Reward");
        layout.put("yFormat", "RATIO");
        layout.put("title", "P&L Booked on Risk/Reward");

        return ChartConfig
                .builder()
                .id("bar-chart-1")
                .type(ChartType.BAR)
                .category(ChartCategory.SERIES)
                .mode(ChartMode.SERIES)
                .source(Source.SYSTEM)
                .layout(layout)
                .xMetric(new XMetric("date", "Date", SemanticType.NUMBER))
                .series(
                        List.of(
                                SeriesConfig
                                        .builder()
                                        .field("riskReward")
                                        .name("Risk/Reward")
                                        .type(SemanticType.NUMBER)
                                        .colorRules(
                                                List.of(
                                                        ColorRuleConfig
                                                                .builder()
                                                                .operator("greaterThan")
                                                                .value(0.6)
                                                                .color("var(--success)")
                                                                .label("Reward Taken")
                                                                .build(),

                                                        ColorRuleConfig
                                                                .builder()
                                                                .operator("greaterThan")
                                                                .value(0.0)
                                                                .color("var(--warning)")
                                                                .label("Breakeven")
                                                                .build(),

                                                        ColorRuleConfig
                                                                .builder()
                                                                .operator("lessThan")
                                                                .value(0.6)
                                                                .color("var(--error)")
                                                                .label("Risk Taken")
                                                                .build()
                                                )
                                        )
                                        .build()
                        ))
                .sort(new SortConfig(null, "none"))
                .filters(new ArrayList<>())
                .selection(new SelectionConfig(null, null))
                .build();
    }

    private ChartConfig lineChart() {

        Map<String, Object> layout = layoutRegistry.get("line");
        layout.put("xTitleText", "Date");
        layout.put("xFormat", "hh:mm:ss A");
        layout.put("yTitleText", "Pnl");
        layout.put("yFormat", "CURRENCY");
        layout.put("title", "P&L Over Time");


        return ChartConfig.builder()
                          .id("donut-chart-1")
                          .type(ChartType.DONUT)
                          .category(ChartCategory.GROUP)
                          .source(Source.SYSTEM)
                          .layout(layout)
                          .xMetric(new XMetric("entryTime", "Entry Time", SemanticType.NUMBER))
                          .series(List.of(
                                  SeriesConfig.builder()
                                              .field("pnl")
                                              .name("Pnl")
                                              .type(SemanticType.NUMBER)
                                              .label("Pnl")
                                              .color("var(--cyan)")
                                              .markerColor("var(--cyan)")
                                              .areaColor("var(--cyan)")
                                              .build()
                          ))
                          .sort(new SortConfig(null, "none"))
                          .filters(new ArrayList<>())
                          .selection(new SelectionConfig(null, null))
                          .build();
    }

    private ChartConfig pieChart() {
        Map<String, Object> layout = layoutRegistry.get("pie");
        layout.put("format", "PERCENT");

        return ChartConfig.builder()
                          .id("donut-chart-1")
                          .type(ChartType.DONUT)
                          .category(ChartCategory.GROUP)
                          .source(Source.SYSTEM)
                          .layout(layout)
                          .series(List.of(
                                  SeriesConfig.builder()
                                              .field("WIN_RATE")
                                              .name("Win Rate")
                                              .type(SemanticType.NUMBER)
                                              .ast(function("WIN_RATE")) // AST
                                              .formula("WIN_RATE()")
                                              .dependencies(List.of("pnl"))
                                              .label("Wins")
                                              .color("var(--info)")
                                              .build(),

                                  SeriesConfig.builder()
                                              .field("LOSS_RATE")
                                              .name("Loss Rate")
                                              .type(SemanticType.NUMBER)
                                              .ast(function("LOSS_RATE")) // AST
                                              .formula("LOSS_RATE()")
                                              .dependencies(List.of("pnl"))
                                              .label("Losses")
                                              .color("var(--warning)")
                                              .build()
                          ))
                          .build();
    }

    private ChartConfig radialBarChart() {

        Map<String, Object> layout = layoutRegistry.get("radialBar");
        layout.put("format", "PERCENT");

        return ChartConfig.builder()
                          .id("radialBar-chart-1")
                          .type(ChartType.RADIAL_BAR)
                          .category(ChartCategory.GROUP)
                          .source(Source.SYSTEM)
                          .layout(layout)
                          .series(List.of(
                                  SeriesConfig.builder()
                                              .field("WIN_RATE")
                                              .name("Win Rate")
                                              .type(SemanticType.NUMBER)
                                              .ast(function("WIN_RATE")) // AST
                                              .formula("WIN_RATE()")
                                              .dependencies(List.of("pnl"))
                                              .label("Wins")
                                              .color("var(--info)")
                                              .build(),

                                  SeriesConfig.builder()
                                              .field("LOSS_RATE")
                                              .name("Loss Rate")
                                              .type(SemanticType.NUMBER)
                                              .ast(function("LOSS_RATE")) // AST
                                              .formula("LOSS_RATE()")
                                              .dependencies(List.of("pnl"))
                                              .label("Losses")
                                              .color("var(--warning)")
                                              .build()
                          ))
                          .build();
    }
}