package com.example.find_my_edge.workspace.registry;

import com.example.find_my_edge.common.config.ColorRuleConfig;

import com.example.find_my_edge.workspace.config.stat.StatConfig;
import com.example.find_my_edge.workspace.features.StatService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.example.find_my_edge.common.config.builder.AstConfigBuilder.*;

@Component
@RequiredArgsConstructor
public class StatRegistry {

    private final StatService statService;

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

        statService.deleteByUserId();
        statService.createAll(this.stats, new ArrayList<>(this.statsOrder));
    }

    public List<StatConfig> getDefaultStats() {   // call on user signup for default stats
        return statsOrder.stream()
                         .map(stats::get)
                         .toList();
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
                         .ast(function("SUM", key("pnl")))
                         .format("COMPACT_CURRENCY_SIGNED")
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
                         .ast(function("AVG", key("riskReward")))
                         .format("RATIO")
                         .colorRules(new ArrayList<>())
                         .build();
    }

    private StatConfig avgHoldingTime() {
        return StatConfig.builder()
                         .id("stat-3")
                         .title("Avg Holding Time")
                         .type("duration")
                         .ast(function("AVG", key("duration")))
                         .format("HH:mm:ss")
                         .colorRules(new ArrayList<>())
                         .build();
    }

    private StatConfig maxProfit() {
        return StatConfig.builder()
                         .id("stat-4")
                         .title("Max Profit")
                         .type("number")
                         .ast(function("MAX", key("pnl")))
                         .format("CURRENCY_SIGNED")
                         .colorRules(new ArrayList<>())
                         .build();
    }

    private StatConfig maxLoss() {
        return StatConfig.builder()
                         .id("stat-5")
                         .title("Max Loss")
                         .type("number")
                         .ast(function("MIN", key("pnl")))
                         .format("CURRENCY_SIGNED")
                         .colorRules(new ArrayList<>())
                         .build();
    }

    private StatConfig winRate() {
        return StatConfig.builder()
                         .id("stat-6")
                         .title("Win Rate")
                         .type("number")
                         .ast(function("WIN_RATE"))
                         .format("PERCENT")
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