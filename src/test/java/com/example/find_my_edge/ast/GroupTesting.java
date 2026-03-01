package com.example.find_my_edge.ast;

import com.example.find_my_edge.analytics.config.GroupConfig;
import com.example.find_my_edge.analytics.engine.group.GroupBuilder;
import com.example.find_my_edge.analytics.engine.group.GroupCompiler;
import com.example.find_my_edge.analytics.engine.group.model.Group;
import com.example.find_my_edge.domain.trade.model.Trade;
import com.example.find_my_edge.domain.trade.service.TradeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class GroupTesting {

    @Autowired
    private TradeService tradeService;

    @Autowired
    private GroupBuilder groupBuilder;

    @Autowired
    private GroupCompiler groupCompiler;

    @Test
    public void grouping() {

        List<Trade> all = tradeService.getAll();

        Map<String, Trade> tradesById = new HashMap<>();
        List<String> tradeOrder = new ArrayList<>();

        all.forEach(t -> {
            tradesById.put(t.getId(), t);
            tradeOrder.add(t.getId());
        });

        List<Group> groups = groupBuilder.buildGroups(
                tradeOrder,
                tradesById,
                GroupConfig.builder()
                           .type("dateBucket")
                           .key("date")
                           .unit("month")
                           .build(),
                (t, k) -> t.getValues().get(k),
                groupCompiler
        );

        groups.forEach(System.out::println);
    }
}
