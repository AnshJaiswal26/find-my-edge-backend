package com.example.find_my_edge.ast;

import com.example.find_my_edge.AuthTestClient;
import com.example.find_my_edge.analytics.ast.context.SchemaType;
import com.example.find_my_edge.analytics.ast.executor.AggregateExecutor;
import com.example.find_my_edge.analytics.ast.parser.AstBuilder;
import com.example.find_my_edge.analytics.ast.parser.AstPipeline;
import com.example.find_my_edge.analytics.engine.context.TradeContextBuilder;
import com.example.find_my_edge.analytics.execution.AggregateExecutionService;
import com.example.find_my_edge.bootstrap.dto.BootstrapResponse;
import com.example.find_my_edge.common.util.JsonUtil;
import com.example.find_my_edge.schema.dto.SchemaResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
@Import(AuthTestClient.class)
public class AggregateEngineTest {

    @Autowired
    private AggregateExecutionService aggregateExecutionService;

    @Autowired
    private TradeContextBuilder builder;

    @Autowired
    private AuthTestClient authTestClient;

    @Autowired
    private AggregateExecutor executor;

    @Autowired
    private AstPipeline astPipeline;

    @Autowired
    private JsonUtil jsonUtil;


    @Test
    void aggregate() throws Exception {
        BootstrapResponse call = authTestClient.doCall(
                "annu@gmail.com",
                "annu@123",
                "/api/app/init",
                BootstrapResponse.class
        );

        Map<String, Map<String, Object>> raw = call.getTradesById();
        Map<String, Map<String, Object>> computed = call.getDerivedByTradeId();
        List<String> tradeOrder = call.getTradesOrder();
        Map<String, SchemaResponse> schemasById = call.getSchemasById();

        System.out.println(jsonUtil.pretty(raw));
        System.out.println(jsonUtil.pretty(computed));

        Object execute =
                executor.execute(
                    astPipeline.buildAst("SUM_IF(@{pnl}, @{direction} == \"PUT\")").getAstNode(),
                        (i, field) -> {
                            String s = tradeOrder.get(i);
                            Object o = raw.get(s).get(field);
                            if (o == null) {
                                return computed.get(s).get(field);
                            }
                            return o;
                        },
                        tradeOrder::size,
                        field -> {
                            SchemaResponse schemaResponse = schemasById.get(field);
                            return new SchemaType(
                                    schemaResponse.getDisplay().getFormat(),
                                    schemaResponse.getSemanticType().toJson()
                            );
                        }
                );

        System.out.println((double) execute);
    }
}
