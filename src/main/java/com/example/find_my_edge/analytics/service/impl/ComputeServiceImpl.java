package com.example.find_my_edge.analytics.service.impl;

import com.example.find_my_edge.analytics.ast.exception.AstException;
import com.example.find_my_edge.analytics.config.GroupConfig;
import com.example.find_my_edge.analytics.engine.aggregate.AggregateComputeEngine;
import com.example.find_my_edge.analytics.engine.context.TradeContextBuilder;
import com.example.find_my_edge.analytics.engine.dataSet.GroupTradeDataset;
import com.example.find_my_edge.analytics.engine.dataSet.TradeDataset;
import com.example.find_my_edge.analytics.engine.group.GroupBuilder;
import com.example.find_my_edge.analytics.engine.group.model.Group;
import com.example.find_my_edge.analytics.model.ComputationContext;
import com.example.find_my_edge.analytics.service.ComputeService;
import com.example.find_my_edge.analytics.execution.GroupSeriesExecutionService;
import com.example.find_my_edge.common.config.uiconfigs.AstConfig;
import com.example.find_my_edge.schema.model.Schema;
import com.example.find_my_edge.workspace.config.chart.SeriesConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
public class ComputeServiceImpl implements ComputeService {

    private final AggregateComputeEngine aggregateComputeEngine;
    private final TradeContextBuilder tradeContextBuilder;

    private final GroupBuilder groupBuilder;

    private final GroupSeriesExecutionService groupSeriesExecutionService;


}