package com.example.find_my_edge.workspace.config.chart.builder;

import com.example.find_my_edge.analytics.config.GroupConfig;
import com.example.find_my_edge.analytics.config.GroupRangeConfig;
import com.example.find_my_edge.analytics.config.SortConfig;
import com.example.find_my_edge.common.config.ColorRuleConfig;
import com.example.find_my_edge.workspace.config.chart.ChartConfig;
import com.example.find_my_edge.workspace.config.chart.ChartMetaConfig;
import com.example.find_my_edge.workspace.config.chart.SeriesConfig;
import com.example.find_my_edge.workspace.registry.LayoutRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChartConfigBuilder {

    private final LayoutRegistry layoutRegistry;

    public ChartConfig buildBarChart(
            SeriesConfig x,
            List<SeriesConfig> y,
            GroupConfig groupSpec,
            Map<String, Object> layoutOverrides
    ) {
        ChartConfig config = new ChartConfig();

        // ✅ meta
        config.setMeta(ChartMetaConfig.builder()
                                      .id(UUID.randomUUID().toString())
                                      .type("bar")
                                      .category("series")
                                      .build());

        // ✅ layout (merge like frontend)
        Map<String, Object> layout = layoutRegistry.get("bar");

        layout.put("xFormat", defaultFormat(x.getType()));
        layout.put("yFormat", defaultFormat(y.getFirst().getType()));

        if (layoutOverrides != null) {
            layout.putAll(layoutOverrides);
        }

        config.setLayout(layout);

        // ✅ sort
        config.setSort(new SortConfig(null, "none"));

        // ✅ group
        config.setGroupSpec(groupSpec);

        // ✅ filters
        config.setFilters(new ArrayList<>());

        // ✅ selection
        config.setSelection(new GroupRangeConfig(null, null));

        // ✅ x series
        config.setXSeriesConfig(buildX(x));

        // ✅ y series
        config.setYSeriesConfig(
                y.stream().map(this::buildBarSeries).toList()
        );

        return config;
    }


    private SeriesConfig buildBarSeries(SeriesConfig s) {
        SeriesConfig config = new SeriesConfig();

        config.setKey(s.getKey());
        config.setName(s.getName() != null ? s.getName() : s.getKey());
        config.setType(s.getType() != null ? s.getType() : "number");
        config.setAst(s.getAst());

        if (s.getColorRules() == null || s.getColorRules().isEmpty()) {
            config.setColorRules(List.of(
                    ColorRuleConfig.builder()
                                   .operator("always")
                                   .value(0.0)
                                   .color("var(--info)")
                                   .build()
            ));
        } else {
            config.setColorRules(s.getColorRules());
        }

        return config;
    }


    public ChartConfig buildLineChart(
            SeriesConfig x,
            List<SeriesConfig> y,
            GroupConfig groupSpec,
            Map<String, Object> layoutOverrides,
            String category
    ) {
        ChartConfig config = new ChartConfig();

        // ✅ meta
        config.setMeta(ChartMetaConfig.builder()
                                      .id(UUID.randomUUID().toString())
                                      .type("line")
                                      .category(category != null ? category : "series")
                                      .build());

        // ✅ layout (merge defaults + computed + overrides)
        Map<String, Object> layout = layoutRegistry.get("line");

        layout.put("xFormat", defaultFormat(x.getType()));
        layout.put("yFormat", defaultFormat(y.getFirst().getType()));

        if (layoutOverrides != null) {
            layout.putAll(layoutOverrides);
        }

        config.setLayout(layout);

        // ✅ group
        config.setGroupSpec(groupSpec);

        // ✅ sort
        config.setSort(new SortConfig(null, "none"));

        // ✅ filters
        config.setFilters(new ArrayList<>());

        // ✅ selection
        config.setSelection(new GroupRangeConfig(null, null));

        // ✅ x series
        config.setXSeriesConfig(buildX(x));

        // ✅ y series (line-specific)
        config.setYSeriesConfig(
                y.stream().map(this::buildLineSeries).toList()
        );

        return config;
    }

    private SeriesConfig buildLineSeries(SeriesConfig s) {
        SeriesConfig config = new SeriesConfig();

        config.setKey(s.getKey());
        config.setName(s.getName() != null ? s.getName() : s.getKey());
        config.setType(s.getType() != null ? s.getType() : "number");
        config.setAst(s.getAst());

        // 🔥 line-specific fields
        config.setLabel(
                s.getLabel() != null
                ? s.getLabel()
                : (s.getName() != null ? s.getName() : s.getKey())
        );

        String baseColor = s.getColor() != null ? s.getColor() : "var(--info)";

        config.setColor(baseColor);
        config.setMarkerColor(
                s.getMarkerColor() != null ? s.getMarkerColor() : baseColor
        );
        config.setAreaColor(
                s.getAreaColor() != null ? s.getAreaColor() : baseColor
        );

        return config;
    }

    public ChartConfig buildDonutChart(
            List<SeriesConfig> series,
            GroupConfig groupSpec,
            Map<String, Object> layoutOverrides
    ) {
        ChartConfig config = new ChartConfig();

        // ✅ meta
        config.setMeta(ChartMetaConfig.builder()
                                      .id(UUID.randomUUID().toString())
                                      .type("donut")
                                      .category("group")
                                      .build());

        // ✅ layout (DEFAULT_LAYOUTS.pie equivalent)
        Map<String, Object> layout = layoutRegistry.get("pie");

        // computed defaults
        String type = series.getFirst().getType();
        layout.put("format", defaultFormat(type));
        layout.put("decimals", 2);

        if (layoutOverrides != null) {
            layout.putAll(layoutOverrides);
        }

        config.setLayout(layout);

        // ✅ group
        config.setGroupSpec(groupSpec);


        // ✅ series (donut-specific)
        config.setSeriesConfig(
                series.stream().map(this::buildDonutSeries).toList()
        );

        return config;
    }

    private SeriesConfig buildDonutSeries(SeriesConfig s) {
        SeriesConfig config = new SeriesConfig();

        config.setKey(s.getKey());
        config.setName(s.getName() != null ? s.getName() : s.getKey());
        config.setType(s.getType() != null ? s.getType() : "number");
        config.setAst(s.getAst());

        // 🔥 donut-specific
        config.setLabel(
                s.getLabel() != null
                ? s.getLabel()
                : (s.getName() != null ? s.getName() : s.getKey())
        );

        config.setColor(
                s.getColor() != null ? s.getColor() : "var(--info)"
        );

        return config;
    }

    public ChartConfig buildRadialBarChart(
            List<SeriesConfig> series,
            GroupConfig groupSpec,
            Map<String, Object> layoutOverrides
    ) {
        ChartConfig config = new ChartConfig();

        // ✅ meta
        config.setMeta(ChartMetaConfig.builder()
                                      .id(UUID.randomUUID().toString())
                                      .type("radialBar")
                                      .category("group")
                                      .build());

        // ✅ layout (DEFAULT_LAYOUTS.radialBar)
        Map<String, Object> layout = layoutRegistry.get("radialBar");

        if (layoutOverrides != null) {
            layout.putAll(layoutOverrides);
        }

        config.setLayout(layout);

        // ✅ group
        config.setGroupSpec(groupSpec);

        // ✅ series (radial-specific)
        config.setSeriesConfig(
                series.stream().map(this::buildRadialSeries).toList()
        );

        return config;
    }

    private SeriesConfig buildRadialSeries(SeriesConfig s) {
        SeriesConfig config = new SeriesConfig();

        config.setKey(s.getKey());
        config.setName(s.getName() != null ? s.getName() : s.getKey());
        config.setType(s.getType() != null ? s.getType() : "number");

        // 🔥 format + decimals (unique to radial)
        String type = config.getType();
        config.setFormat(
                s.getFormat() != null ? s.getFormat() : defaultFormat(type)
        );

        config.setDecimals(
                s.getDecimals() != null ? s.getDecimals() : 2
        );

        config.setAst(s.getAst());

        // label fallback
        config.setLabel(
                s.getLabel() != null
                ? s.getLabel()
                : (s.getName() != null ? s.getName() : s.getKey())
        );

        // color fallback
        config.setColor(
                s.getColor() != null ? s.getColor() : "var(--info)"
        );

        return config;
    }

    private SeriesConfig buildX(SeriesConfig s) {
        SeriesConfig config = new SeriesConfig();

        config.setKey(s.getKey() != null ? s.getKey() : "");
        config.setName(s.getName() != null ? s.getName() : "");
        config.setType(s.getType() != null ? s.getType() : "number");

        return config;
    }

    private String defaultFormat(String type) {
        return switch (type == null ? "number" : type) {
            case "number" -> "COMPACT_NUMBER";
            case "currency" -> "CURRENCY";
            case "percent" -> "PERCENT";
            default -> "DEFAULT";
        };
    }
}