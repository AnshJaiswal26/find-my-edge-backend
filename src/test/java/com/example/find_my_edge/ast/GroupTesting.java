package com.example.find_my_edge.ast;

import com.example.find_my_edge.analytics.config.GroupConfig;
import com.example.find_my_edge.analytics.engine.context.TradeContextBuilder;
import com.example.find_my_edge.analytics.engine.group.GroupBuilder;
import com.example.find_my_edge.analytics.engine.group.model.Group;
import com.example.find_my_edge.analytics.model.ComputationContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.List;
import java.util.Map;

@SpringBootTest
public class GroupTesting {

    @Autowired
    private GroupBuilder groupBuilder;

    @Autowired
    private TradeContextBuilder builder;


    @Test
    public void grouping() {

        ComputationContext computationContext = builder.buildContext();

        Map<String, Map<String, Object>> raw = computationContext.getRaw();
        Map<String, Map<String, Object>> computed = computationContext.getComputed();
        List<String> tradeOrder = computationContext.getTradeOrder();

        List<Group> groups = groupBuilder.buildGroups(
                tradeOrder,
                GroupConfig.builder()
                           .type("dateBucket")
                           .field("date")
                           .unit("month")
                           .build(),

                (tradeId, key) -> {
                    Object value = raw.get(tradeId).get(key);
                    if (value == null) {
                        return computed.get(tradeId).get(key);
                    }
                    return value;
                }
        );

        groups.forEach(System.out::println);
    }
}
