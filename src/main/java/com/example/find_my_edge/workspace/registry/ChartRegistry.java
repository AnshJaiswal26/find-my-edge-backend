package com.example.find_my_edge.workspace.registry;

import com.example.find_my_edge.analytics.config.SortConfig;
import com.example.find_my_edge.common.config.ColorRuleConfig;
import com.example.find_my_edge.workspace.config.chart.ChartConfig;
import com.example.find_my_edge.workspace.config.chart.ChartMetaConfig;
import com.example.find_my_edge.workspace.config.chart.SeriesConfig;
import com.example.find_my_edge.workspace.config.chart.SelectionConfig;
import com.example.find_my_edge.workspace.enums.ChartCategory;
import com.example.find_my_edge.workspace.enums.ChartMode;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.example.find_my_edge.common.config.builder.AstConfigBuilder.*;

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

    public List<String> getOrder() {
        return chartOrder.stream().toList();
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
                                      .id("bar-chart-1")
                                      .type("bar")
                                      .category(ChartCategory.SERIES.key())
                                      .mode(ChartMode.SERIES.toString())
                                      .build());

        Map<String, Object> layout = layoutRegistry.get("bar");

        layout.put("xTitleText", "Trades");
        layout.put("xFormat", "YYYY-MM-DD");
        layout.put("yTitleText", "Risk/Reward");
        layout.put("yFormat", "RATIO");
        layout.put("title", "P&L Booked on Risk/Reward");

        config.setLayout(layout);

        config.setSort(new SortConfig(null, "none"));
        config.setFilters(new ArrayList<>());
        config.setSelection(new SelectionConfig(null, null));

        config.setXSeries(SeriesConfig.builder()
                                      .key("date")
                                      .name("Date")
                                      .type("date")
                                      .build());

        config.setYSeries(List.of(
                SeriesConfig.builder()
                            .key("riskReward")
                            .name("Risk/Reward")
                            .type("number")
                            .ast(null)
                            .colorRules(
                                    List.of(
                                            ColorRuleConfig.builder()
                                                           .operator("greaterThan")
                                                           .value(0.6)
                                                           .color("var(--success)")
                                                           .label("Reward Taken")
                                                           .build(),

                                            ColorRuleConfig.builder()
                                                           .operator("greaterThan")
                                                           .value(0.0)
                                                           .color("var(--warning)")
                                                           .label("Breakeven")
                                                           .build(),

                                            ColorRuleConfig.builder()
                                                           .operator("lessThan")
                                                           .value(0.6)
                                                           .color("var(--error)")
                                                           .label("Risk Taken")
                                                           .build()
                                    )
                            )
                            .build()
        ));

        return config;
    }

    private ChartConfig lineChart() {
        ChartConfig config = new ChartConfig();

        config.setMeta(ChartMetaConfig.builder()
                                      .id("line-chart-1")
                                      .type("line")
                                      .category(ChartCategory.SERIES.key())
                                      .mode(ChartMode.SERIES.toString())
                                      .build());

        Map<String, Object> layout = layoutRegistry.get("line");

        layout.put("xTitleText", "Date");
        layout.put("xFormat", "hh:mm:ss A");
        layout.put("yTitleText", "Pnl");
        layout.put("yFormat", "CURRENCY");
        layout.put("title", "P&L Over Time");

        config.setLayout(layout);

        config.setSort(new SortConfig(null, "none"));
        config.setFilters(new ArrayList<>());
        config.setSelection(new SelectionConfig(null, null));

        config.setXSeries(SeriesConfig.builder()
                                      .key("entryTime")
                                      .name("Entry Time")
                                      .type("time")
                                      .build());

        config.setYSeries(List.of(
                SeriesConfig.builder()
                            .key("pnl")
                            .name("Pnl")
                            .type("number")
                            .label("Pnl")
                            .color("var(--cyan)")
                            .markerColor("var(--cyan)")
                            .areaColor("var(--cyan)")
                            .build()
        ));

        return config;
    }

    private ChartConfig pieChart() {
        ChartConfig config = new ChartConfig();

        config.setMeta(ChartMetaConfig.builder()
                                      .id("donut-chart-1")
                                      .type("donut")
                                      .category(ChartCategory.GROUP.key())
                                      .build());

        Map<String, Object> layout = layoutRegistry.get("pie");

        layout.put("format", "PERCENT");

        config.setSeriesConfig(
                List.of(
                        SeriesConfig.builder()
                                    .key("WIN_RATE")
                                    .name("Win Rate")
                                    .type("number")
                                    .ast(function("WIN_RATE")) // AST
                                    .label("Wins")
                                    .color("var(--info)")
                                    .build(),

                        SeriesConfig.builder()
                                    .key("LOSS_RATE")
                                    .name("Loss Rate")
                                    .type("number")
                                    .ast(function("LOSS_RATE")) // AST
                                    .label("Losses")
                                    .color("var(--warning)")
                                    .build()
                )
        );

        config.setLayout(layout);

        return config;
    }

    private ChartConfig radialBarChart() {
        ChartConfig config = new ChartConfig();

        config.setMeta(ChartMetaConfig.builder()
                                      .id("radialBar-chart-1")
                                      .type("radialBar")
                                      .category(ChartCategory.GROUP.key())
                                      .build());

        Map<String, Object> layout = layoutRegistry.get("radialBar");

        layout.put("format", "PERCENT");

        config.setLayout(layout);

        config.setSeriesConfig(
                List.of(
                        SeriesConfig.builder()
                                    .key("WIN_RATE")
                                    .name("Win Rate")
                                    .type("number")
                                    .ast(function("WIN_RATE")) // AST
                                    .label("Wins")
                                    .color("var(--info)")
                                    .build(),

                        SeriesConfig.builder()
                                    .key("LOSS_RATE")
                                    .name("Loss Rate")
                                    .type("number")
                                    .ast(function("LOSS_RATE")) // AST
                                    .label("Losses")
                                    .color("var(--warning)")
                                    .build()
                )
        );

        return config;
    }
}