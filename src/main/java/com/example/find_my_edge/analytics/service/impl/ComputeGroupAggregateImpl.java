package com.example.find_my_edge.analytics.service.impl;

import com.example.find_my_edge.analytics.config.GroupConfig;
import com.example.find_my_edge.analytics.engine.group.GroupBuilder;
import com.example.find_my_edge.analytics.service.ComputeGroupAggregate;
import com.example.find_my_edge.analytics.service.ComputeService;
import com.example.find_my_edge.schema.model.SchemaBundle;
import com.example.find_my_edge.schema.service.SchemaService;
import com.example.find_my_edge.trade.model.Trade;
import com.example.find_my_edge.trade.model.TradeBundle;
import com.example.find_my_edge.trade.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComputeGroupAggregateImpl implements ComputeGroupAggregate {

    private final ComputeService computeService;

    private final GroupBuilder groupBuilder;

    private final TradeService tradeService;

    private final SchemaService schemaService;

    @Override
    public Object compute(GroupConfig groupConfig) {

        TradeBundle tradeBundle = tradeService.getTradeBundle();

        SchemaBundle schemaBundle= schemaService.getAll();

//        groupBuilder.buildGroups(
//                tradeBundle.getTradeOrder(),
//                tradeBundle.getTradesById(),
//                groupConfig,
//                (trade, key) ->
//
//        )
        return null;
    }
}
