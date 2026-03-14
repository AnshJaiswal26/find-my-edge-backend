package com.example.find_my_edge.analytics.compute;

import com.example.find_my_edge.analytics.engine.aggregate.AggregateComputeEngine;
import com.example.find_my_edge.analytics.engine.dataSet.GroupTradeDataset;
import com.example.find_my_edge.analytics.engine.dataSet.TradeDataset;
import com.example.find_my_edge.analytics.engine.group.model.Group;
import com.example.find_my_edge.analytics.execution.GroupSeriesExecutionService;
import com.example.find_my_edge.analytics.model.ChartResult;
import com.example.find_my_edge.analytics.model.ComputationContext;
import com.example.find_my_edge.analytics.execution.AggregateExecutionService;
import com.example.find_my_edge.common.util.JsonUtil;
import com.example.find_my_edge.workspace.config.chart.ChartConfig;
import com.example.find_my_edge.workspace.config.chart.SeriesConfig;
import com.example.find_my_edge.workspace.enums.ChartCategory;
import com.example.find_my_edge.workspace.enums.ChartMode;
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


    public ChartResult computeGroupAggregateChart(
            ChartConfig chartConfig,
            ComputationContext ctx
    ) {

        List<Group> groups =
                groupComputeService.buildGroups(ctx, chartConfig.getGroup(), false);

        boolean useAst = chartConfig.getSource() == Source.SYSTEM;

        Map<String, Map<String, Double>> matrix =
                groupSeriesExecutionService.execute(

                        groups,
                        chartConfig.getSeriesById().values(),

                        Group::getGroupId,
                        SeriesConfig::getId,

                        (group, series) -> {

                            TradeDataset dataset =
                                    new GroupTradeDataset(ctx, group.getTradeIds());

                            return aggregateComputeEngine.computedAggregate(
                                    useAst ? series.getAst() : null,
                                    !useAst ? series.getFormula() : null,
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

    public void computeSingleAggregateChart(
            ChartConfig chart,
            ComputationContext computationContext
    ) {

        boolean useAst = chart.getSource() == Source.SYSTEM;

        aggregateExecutionService.executeAggregate(
                chart.getSeriesById().values(),
                SeriesConfig::getId,
                (id, cfg) -> !useAst ? cfg.getFormula() : null,
                (id, cfg) -> useAst ? cfg.getAst() : null,
                (id, value) ->
                        chart.getSeriesById().get(id).setValue(value),
                computationContext
        );

    }

    public Map<String, ChartResult> computeCharts(
            Map<String, ChartConfig> chartsById,
            ComputationContext ctx
    ) {

        Map<String, ChartResult> resultMap = new HashMap<>();

        for (ChartConfig chart : chartsById.values()) {

            if (chart.getMode() == ChartMode.GROUP_AGGREGATE) {

                ChartResult result = computeGroupAggregateChart(chart, ctx);
                resultMap.put(chart.getId(), result);

            } else if(chart.getCategory() == ChartCategory.GROUP) {
                computeSingleAggregateChart(chart, ctx);
            }
        }

        return resultMap;
    }

    public Map<String, ChartConfig> resolveAffectedCharts(
            Map<String, ChartConfig> charts,
            Set<String> affectedSchemas
    ) {

        Map<String, ChartConfig> result = new HashMap<>();

        for (ChartConfig chart : charts.values()) {

            boolean affected = chart.getSeriesById().values()
                                    .stream()
                                    .anyMatch(series ->
                                                      series.getDependencies() != null &&
                                                      !Collections.disjoint(series.getDependencies(), affectedSchemas)
                                    );

            if (affected) {
                result.put(chart.getId(), chart);
            }
        }

        return result;
    }
}