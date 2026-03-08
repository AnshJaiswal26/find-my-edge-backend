package com.example.find_my_edge.workspace.registry;

import com.example.find_my_edge.common.config.uiconfigs.ColorRuleConfig;

import com.example.find_my_edge.workspace.config.stat.StatConfig;
import com.example.find_my_edge.workspace.enums.Source;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.example.find_my_edge.common.builder.AstConfigBuilder.*;

@Component
@RequiredArgsConstructor
public class StatRegistry {

    private Map<String, StatConfig> stats;
    private Set<String> statsOrder;

    @PostConstruct
    public void init() {
        Map<String, StatConfig> tempMap = new HashMap<>();
        Set<String> tempOrder = new LinkedHashSet<>();

        buildSystemStats().forEach(stat -> {
            tempOrder.add(stat.getId());
            tempMap.put(stat.getId(), stat);
        });

        this.stats = Map.copyOf(tempMap);
        this.statsOrder = Set.copyOf(tempOrder);
    }

    public List<StatConfig> getAll() {   // call on user signup for default stats
        return statsOrder.stream()
                         .map(stats::get)
                         .toList();
    }

    public Map<String, StatConfig> getStatsById() {   // call on user signup for default stats
        return stats;
    }

    public List<String> getOrder() {
        return statsOrder.stream().toList();
    }

    public boolean has(String statId) {
        return statsOrder.contains(statId);
    }

    private List<StatConfig> buildSystemStats() {
        return List.of(
                pnl(),
                avgRiskReward(),
                avgHoldingTime(),
                maxProfit(),
                maxLoss(),
                winRate()
        );
    }

    private StatConfig pnl() {
        return StatConfig.builder()
                         .id("stat-1")
                         .title("PnL")
                         .type("number")
                         .ast(function("SUM", field("pnl")))
                         .formula("SUM(pnl)")
                         .dependencies(List.of("pnl"))
                         .format("COMPACT_CURRENCY_SIGNED")
                         .source(Source.SYSTEM)
                         .colorRules(List.of(
                                 colorRule("greaterThan", 0.0, "var(--success)"),
                                 colorRule("lessThan", 0.0, "var(--error)")
                         ))
                         .build();
    }

    private StatConfig avgRiskReward() {
        return StatConfig.builder()
                         .id("stat-2")
                         .title("Avg Risk/Reward")
                         .type("number")
                         .ast(function("AVG", field("riskReward")))
                         .formula("AVG(riskReward)")
                         .dependencies(List.of("riskReward"))
                         .format("RATIO")
                         .source(Source.SYSTEM)
                         .colorRules(new ArrayList<>())
                         .build();
    }

    private StatConfig avgHoldingTime() {
        return StatConfig.builder()
                         .id("stat-3")
                         .title("Avg Holding Time")
                         .type("duration")
                         .ast(function("AVG", field("duration")))
                         .formula("AVG(duration)")
                         .dependencies(List.of("duration"))
                         .format("HH:mm:ss")
                         .source(Source.SYSTEM)
                         .colorRules(new ArrayList<>())
                         .build();
    }

    private StatConfig maxProfit() {
        return StatConfig.builder()
                         .id("stat-4")
                         .title("Max Profit")
                         .type("number")
                         .ast(function("MAX", field("pnl")))
                         .formula("MAX(pnl)")
                         .dependencies(List.of("pnl"))
                         .format("CURRENCY_SIGNED")
                         .source(Source.SYSTEM)
                         .colorRules(new ArrayList<>())
                         .build();
    }

    private StatConfig maxLoss() {
        return StatConfig.builder()
                         .id("stat-5")
                         .title("Max Loss")
                         .type("number")
                         .ast(function("MIN", field("pnl")))
                         .formula("MIN(pnl)")
                         .dependencies(List.of("pnl"))
                         .format("CURRENCY_SIGNED")
                         .source(Source.SYSTEM)
                         .colorRules(new ArrayList<>())
                         .build();
    }

    private StatConfig winRate() {
        return StatConfig.builder()
                         .id("stat-6")
                         .title("Win Rate")
                         .type("number")
                         .ast(function("WIN_RATE"))
                         .formula("WIN_RATE()")
                         .dependencies(List.of("pnl"))
                         .format("PERCENT")
                         .source(Source.SYSTEM)
                         .colorRules(new ArrayList<>())
                         .build();
    }


    private ColorRuleConfig colorRule(String operator, Double value, String color) {
        return ColorRuleConfig.builder()
                              .operator(operator)
                              .value(value)
                              .color(color)
                              .build();
    }
}