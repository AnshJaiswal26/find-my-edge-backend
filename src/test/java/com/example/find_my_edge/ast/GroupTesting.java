package com.example.find_my_edge.ast;

import com.example.find_my_edge.AuthTestClient;
import com.example.find_my_edge.analytics.compute.ChartComputeService;
import com.example.find_my_edge.analytics.config.GroupConfig;
import com.example.find_my_edge.analytics.engine.context.TradeContextBuilder;
import com.example.find_my_edge.analytics.engine.group.GroupBuilder;
import com.example.find_my_edge.analytics.engine.group.model.Group;
import com.example.find_my_edge.analytics.model.ChartResult;
import com.example.find_my_edge.analytics.model.ComputationContext;
import com.example.find_my_edge.bootstrap.dto.BootstrapResponse;
import com.example.find_my_edge.common.util.JsonUtil;
import com.example.find_my_edge.schema.dto.SchemaResponseDto;
import com.example.find_my_edge.schema.mapper.SchemaDtoMapper;
import com.example.find_my_edge.schema.mapper.SchemaMapper;
import com.example.find_my_edge.schema.model.Schema;
import com.example.find_my_edge.workspace.config.chart.ChartConfig;
import com.example.find_my_edge.workspace.config.chart.SeriesConfig;
import com.example.find_my_edge.workspace.registry.ChartRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.find_my_edge.common.builder.AstConfigBuilder.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(AuthTestClient.class)
public class GroupTesting {

    @Autowired
    private GroupBuilder groupBuilder;

    @Autowired
    private TradeContextBuilder builder;


    @Autowired
    private JsonUtil jsonUtil;

    @Autowired
    private AuthTestClient authTestClient;

    @Autowired
    private ChartComputeService chartComputeService;

    @Autowired
    private ChartRegistry chartRegistry;

    @Autowired
    private SchemaMapper schemaMapper;

    @Autowired
    private SchemaDtoMapper schemaDtoMapper;

    @Test
    public void grouping() throws Exception {

        BootstrapResponse call = authTestClient.getCall(
                "annu@gmail.com",
                "annu@123",
                "/api/app/init",
                BootstrapResponse.class
        );

        Map<String, Map<String, Object>> raw = call.getTradesById();
        Map<String, Map<String, Object>> computed = call.getDerivedByTradeId();
        List<String> tradeOrder = call.getTradesOrder();

        List<Group> groups = groupBuilder.buildGroups(
                tradeOrder,
                GroupConfig.builder()
                           .type("dateBucket")
                           .field("date")
                           .unit("day")
                           .build(),

                (tradeId, key) -> {
                    Object value = raw.get(tradeId).get(key);
                    if (value == null) {
                        return computed.get(tradeId).get(key);
                    }
                    return value;
                },
                true
        );

        String json = jsonUtil.pretty(groups);

        System.out.println(json);
    }

    @Test
    public void groupingAgg() throws Exception {

        BootstrapResponse call = authTestClient.getCall(
                "annu@gmail.com",
                "annu@123",
                "/api/app/init",
                BootstrapResponse.class
        );

//        ComputationContext computationContext = builder.buildContext();

        Map<String, Map<String, Object>> raw = call.getTradesById();
        Map<String, Map<String, Object>> computed = call.getDerivedByTradeId();
        Map<String, SchemaResponseDto> schemasById = call.getSchemasById();

        ChartConfig chartConfig = chartRegistry.getCharts().get("bar-chart-1");

        chartConfig.setGroup(GroupConfig.builder()
                                        .type("dateBucket")
                                        .field("date")
                                        .unit("day")
                                        .build());

        Map<String, SeriesConfig> seriesById = chartConfig.getSeriesById();
        List<SeriesConfig> series = chartConfig.getSeriesOrder().stream().map(seriesById::get).toList();

        series.getFirst().setAst(function("SUM", binary(field("pnl"), "-", field("charges"))));
        series.getLast().setAst(function("SUM", field("entryPrice")));

        ChartResult chartResult =
                chartComputeService.computeGroupAggregateChart(
                        chartConfig,
                        new ComputationContext(
                                raw,
                                computed,
                                call.getTradesOrder(),
                                schemasById
                                        .entrySet()
                                        .stream()
                                        .collect(Collectors.toMap(
                                                Map.Entry::getKey,
                                                e ->
                                                        jsonUtil.fromJson(jsonUtil.toJson(e.getValue()), Schema.class)
                                        )),
                                call.getSchemasOrder()
                        )
                );

//        List<Group> groups = groupBuilder.buildGroups(
//                tradeOrder,
//                GroupConfig.builder()
//                           .type("dateBucket")
//                           .field("date")
//                           .unit("day")
//                           .build(),
//
//                (tradeId, key) -> {
//                    Object value = raw.get(tradeId).get(key);
//                    if (value == null) {
//                        return computed.get(tradeId).get(key);
//                    }
//                  System.out.println(value);
//                    return value;
//                }
//        );

        String json = jsonUtil.pretty(chartResult);

        System.out.println(json);
    }
}
