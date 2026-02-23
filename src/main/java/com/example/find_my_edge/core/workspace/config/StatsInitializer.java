package com.example.find_my_edge.core.workspace.config;

import com.example.find_my_edge.common.dto.ColorRuleDTO;
import com.example.find_my_edge.common.dto.DisplayDTO;

import com.example.find_my_edge.common.enums.Page;
import com.example.find_my_edge.core.workspace.dto.stat.StatDTO;
import com.example.find_my_edge.core.workspace.features.StatService;
import com.example.find_my_edge.core.workspace.service.WorkspaceService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.example.find_my_edge.ast.builder.AstBuilder.*;

@Component
@RequiredArgsConstructor
public class StatsInitializer {

    private final WorkspaceService workspaceService;

    @PostConstruct
    public void init() {
        buildSystemStats()
                .forEach(stat -> workspaceService.seedStats(Page.DASHBOARD, stat));
    }

    private List<StatDTO> buildSystemStats() {
        return List.of(
                pnl(),
                avgRiskReward(),
                avgHoldingTime(),
                maxProfit(),
                maxLoss(),
                winRate()
        );
    }

    private StatDTO pnl() {
        return StatDTO.builder()
                      .id("stat-1")
                      .title("PnL")
                      .type("NUMBER")
                      .ast(function("SUM", key("pnl")))
                      .format("COMPACT_CURRENCY_SIGNED")
                      .colorRules(List.of(
                              colorRule("greaterThan", 0.0, "var(--success)"),
                              colorRule("lessThan", 0.0, "var(--error)")
                      ))
                      .build();
    }

    private StatDTO avgRiskReward() {
        return StatDTO.builder()
                      .id("stat-2")
                      .title("Avg Risk/Reward")
                      .type("NUMBER")
                      .ast(function("AVG", key("riskReward")))
                      .format("RATIO")
                      .build();
    }

    private StatDTO avgHoldingTime() {
        return StatDTO.builder()
                      .id("stat-3")
                      .title("Avg Holding Time")
                      .type("DURATION")
                      .ast(function("AVG", key("duration")))
                      .format("HH:mm:ss")
                      .build();
    }

    private StatDTO maxProfit() {
        return StatDTO.builder()
                      .id("stat-4")
                      .title("Max Profit")
                      .type("NUMBER")
                      .ast(function("MAX", key("pnl")))
                      .format("CURRENCY_SIGNED")
                      .build();
    }

    private StatDTO maxLoss() {
        return StatDTO.builder()
                      .id("stat-5")
                      .title("Max Loss")
                      .type("NUMBER")
                      .ast(function("MIN", key("pnl")))
                      .format("CURRENCY_SIGNED")
                      .build();
    }

    private StatDTO winRate() {
        return StatDTO.builder()
                      .id("stat-6")
                      .title("Win Rate")
                      .type("NUMBER")
                      .ast(function("WIN_RATE"))
                      .format("PERCENT")
                      .build();
    }


    private ColorRuleDTO colorRule(String operator, Double value, String color) {
        return ColorRuleDTO.builder()
                           .operator(operator)
                           .value(value)
                           .color(color)
                           .build();
    }
}