package com.example.find_my_edge.analytics.compute;

import com.example.find_my_edge.analytics.engine.context.TradeContextBuilder;
import com.example.find_my_edge.analytics.model.ComputationContext;
import com.example.find_my_edge.analytics.execution.AggregateExecutionService;
import com.example.find_my_edge.workspace.config.stat.StatConfig;
import com.example.find_my_edge.workspace.enums.Source;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@AllArgsConstructor
public class StatComputeService {

    private final AggregateExecutionService aggregateExecutionService;

    private final TradeContextBuilder contextBuilder;


    public void computeStat(StatConfig stat) {

        aggregateExecutionService.executeAggregate(
                List.of(stat),
                StatConfig::getId,
                (id, statConfig) -> statConfig.getFormula(),
                (id, statConfig) -> null,
                (id, value) -> stat.setValue(value),
                contextBuilder.buildContext()
        );
    }

    public void computeStats(
            Map<String, StatConfig> statsById,
            ComputationContext computationContext
    ) {

        aggregateExecutionService.executeAggregate(
                statsById.entrySet(),
                Map.Entry::getKey,
                (id, entry) ->
                        entry.getValue().getSource() != Source.SYSTEM
                        ? entry.getValue().getFormula()
                        : null,
                (id, entry) ->
                        entry.getValue().getSource() == Source.SYSTEM
                        ? entry.getValue().getAst()
                        : null,
                (id, value) -> {
                    StatConfig statConfig = statsById.get(id);
                    statConfig.setValue(value);
                },
                computationContext
        );
    }


    public Map<String, Double> computeStats(
            Map<String, StatConfig> statsById,
            Set<String> affectedSchemas,
            ComputationContext ctx
    ) {
        List<StatConfig> stats = resolveStats(statsById, affectedSchemas);

        Map<String, Double> statsValues = new HashMap<>();

        aggregateExecutionService.executeAggregate(
                stats,
                StatConfig::getId,
                (id, stat) -> stat.getSource() != Source.SYSTEM ? stat.getFormula() : null,
                (id, stat) -> stat.getSource() == Source.SYSTEM ? stat.getAst() : null,
                statsValues::put,
                ctx
        );

        return statsValues;
    }

    private List<StatConfig> resolveStats(Map<String, StatConfig> stats, Set<String> affectedSchemas) {

        return stats.values()
                    .stream()
                    .filter(stat ->
                                    stat.getDependencies() != null &&
                                    stat.getDependencies().stream().anyMatch(affectedSchemas::contains))
                    .toList();
    }
}
