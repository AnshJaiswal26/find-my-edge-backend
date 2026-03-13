package com.example.find_my_edge.analytics.compute;

import com.example.find_my_edge.analytics.engine.aggregate.AggregateComputeEngine;
import com.example.find_my_edge.analytics.engine.dataSet.GroupTradeDataset;
import com.example.find_my_edge.analytics.engine.dataSet.TradeDataset;
import com.example.find_my_edge.analytics.engine.group.model.Group;
import com.example.find_my_edge.analytics.execution.GroupSeriesExecutionService;
import com.example.find_my_edge.analytics.model.ChartResult;
import com.example.find_my_edge.analytics.model.ComputationContext;
import com.example.find_my_edge.analytics.model.SeriesUpdate;
import com.example.find_my_edge.analytics.execution.AggregateExecutionService;
import com.example.find_my_edge.workspace.config.chart.ChartConfig;
import com.example.find_my_edge.workspace.config.chart.SeriesConfig;
import com.example.find_my_edge.workspace.enums.Source;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ChartComputeService {

    private final AggregateExecutionService aggregateExecutionService;

    private final GroupComputeService groupComputeService;

    private final GroupSeriesExecutionService groupSeriesExecutionService;

    private final AggregateComputeEngine aggregateComputeEngine;

    public Map<String, Double> computeSeries(
            List<SeriesUpdate> seriesUpdates,
            Map<String, Boolean> systemSeries,
            ComputationContext ctx
    ) {

        List<SeriesConfig> series =
                seriesUpdates.stream()
                             .map(SeriesUpdate::getSeries)
                             .toList();

        Map<String, Double> seriesValues = new HashMap<>();

        aggregateExecutionService.executeAggregate(
                series,
                SeriesConfig::getId,
                (id, s) -> Boolean.FALSE.equals(systemSeries.get(id)) ? s.getFormula() : null,
                (id, s) -> Boolean.TRUE.equals(systemSeries.get(id)) ? s.getAst() : null,
                seriesValues::put,
                ctx
        );

        return seriesValues;
    }

    public List<SeriesUpdate> resolveSeries(
            Map<String, ChartConfig> charts,
            Map<String, Boolean> systemSeries,
            Set<String> affectedSchemas
    ) {

        List<SeriesUpdate> result = new ArrayList<>();

        for (ChartConfig chart : charts.values()) {

            if (chart.getSeries() == null) continue;

            boolean usesAst = chart.getSource() == Source.SYSTEM;

            chart.getSeries()
                 .stream()
                 .filter(series -> series.getDependencies() != null &&
                                   series.getDependencies().stream().anyMatch(affectedSchemas::contains))
                 .forEach(series -> {
                     result.add(new SeriesUpdate(chart.getId(), series));
                     systemSeries.put(series.getId(), usesAst);
                 });
        }

        return result;
    }


    public ChartResult computeChart(
            ChartConfig chartConfig,
            ComputationContext ctx
    ) {

        List<Group> groups =
                groupComputeService.buildGroups(ctx, chartConfig.getGroup());


        Map<String, Map<String, Double>> matrix =
                groupSeriesExecutionService.execute(

                        groups,
                        chartConfig.getSeries(),

                        Group::getGroupId,
                        SeriesConfig::getId,

                        (group, series) -> {

                            TradeDataset dataset =
                                    new GroupTradeDataset(ctx, group.getTradeIds());

                            return aggregateComputeEngine.computedAggregate(
                                    series.getAst(),
                                    series.getFormula(),
                                    ctx.getSchemasById(),
                                    dataset
                            );
                        }
                );


        return ChartResult.builder()
                          .groups(groups)
                          .series(matrix)
                          .build();
    }
}