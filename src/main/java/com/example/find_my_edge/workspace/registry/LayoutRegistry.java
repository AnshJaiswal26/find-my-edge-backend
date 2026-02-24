package com.example.find_my_edge.workspace.registry;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LayoutRegistry {

    private final Map<String, Map<String, Object>> layouts = new HashMap<>();

    @PostConstruct
    public void init() {
        layouts.put("bar", barLayout());
        layouts.put("line", lineLayout());
        layouts.put("pie", pieLayout());
        layouts.put("group", radialBarLayout());

    }

    public Map<String, Object> get(String key) {
        return new HashMap<>(layouts.get(key)); // return copy
    }

    private Map<String, Object> barLayout() {
        Map<String, Object> layout = new HashMap<>(cartesianLayout());

        layout.put("title", "Bar Chart");
        layout.put("horizontal", false);
        layout.put("stacked", false);
        layout.put("stacked100", false);
        layout.put("barRadius", 1);

        return layout;
    }

    private Map<String, Object> lineLayout() {
        Map<String, Object> layout = new HashMap<>(cartesianLayout());

        layout.put("title", "Line Chart");
        layout.put("curve", "smooth");// straight, smooth, stepline
        layout.put("strokeWidth", 2);

        // markers
        layout.put("markerSize", 0);
        layout.put("markerHoverSize", 5);

        // area settings
        layout.put("area", true);
        layout.put("areaGradientHorizontal", false); // or "vertical"
        layout.put("areaOpacityFrom", 0.3);
        layout.put("areaOpacityTo", 0.05);

        return layout;
    }

    private Map<String, Object> pieLayout() {
        Map<String, Object> layout = new HashMap<>(cartesianLayout());

        layout.put("title", "Pie Chart");
        layout.put("donutSize", 70);
        layout.put("gradientType", "gradient");
        layout.put("strokeWidth", 0);
        layout.put("dataLabels", false);

        return layout;
    }

    private Map<String, Object> radialBarLayout() {
        Map<String, Object> layout = new HashMap<>(cartesianLayout());

        layout.put("title", "RadialBar Chart");
        layout.put("hollowSize", 50);
        layout.put("gradientType", "gradient");
        layout.put("trackBackground", "var(--hover)");
        layout.put("strokeWidth", 50);
        layout.put("startAngle", 0);
        layout.put("endAngle", 360);
        layout.put("strokeLineCap", "round");

        return layout;
    }

    private Map<String, Object> cartesianLayout() {
        return Map.ofEntries(
                Map.entry("title", ""),

                Map.entry("chartWidth", 100),

                // general
                Map.entry("tooltip", true),
                Map.entry("dataLabels", false),
                Map.entry("selection", false),

                // grid
                Map.entry("xGrid", false),
                Map.entry("yGrid", true),

                // xaxis
                Map.entry("xTooltip", true),
                Map.entry("xLabels", false),
                Map.entry("xLabelsColor", "var(--text-charts-muted)"),
                Map.entry("xTitleText", "Trades"),
                Map.entry("xTitleColor", "var(--text-charts-muted)"),
                Map.entry("xFormat", ""),
                Map.entry("xDecimals", 2),

                // yaxis
                Map.entry("yLabels", true),
                Map.entry("yLabelsColor", "var(--text-charts-muted)"),
                Map.entry("yTitleText", ""),
                Map.entry("yTitleColor", "var(--text-charts-muted)"),
                Map.entry("yFormat", ""),
                Map.entry("yDecimals", 2),

                // legend
                Map.entry("legend", true),
                Map.entry("legendPosition", "top"),
                Map.entry("legendAlignment", "center")
        );
    }

    private Map<String, Object> groupLayout() {
        return Map.ofEntries(
                Map.entry("title", ""),
                Map.entry("chartWidth", 100),

                Map.entry("tooltip", true),

                // Data label parts
                Map.entry("name", true),
                Map.entry("value", true),

                Map.entry("total", true),
                Map.entry("totalLabel", "SUM"),
                Map.entry("reducer", "SUM"),

                // legend
                Map.entry("legend", true),
                Map.entry("legendPosition", "bottom"),
                Map.entry("legendAlignment", "center")
        );
    }

}