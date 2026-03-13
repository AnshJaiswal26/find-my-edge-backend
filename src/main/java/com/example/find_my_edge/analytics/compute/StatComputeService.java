package com.example.find_my_edge.analytics.compute;

import com.example.find_my_edge.analytics.model.ComputationContext;
import com.example.find_my_edge.analytics.execution.AggregateExecutionService;
import com.example.find_my_edge.workspace.config.stat.StatConfig;
import com.example.find_my_edge.workspace.enums.Source;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class StatComputeService {

    private final AggregateExecutionService aggregateExecutionService;


    public Map<String, Double> computeStats(
            List<StatConfig> stats,
            ComputationContext ctx
    ) {

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
}
