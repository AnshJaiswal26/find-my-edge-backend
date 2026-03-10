package com.example.find_my_edge.analytics.service.impl;

import com.example.find_my_edge.analytics.ast.exception.AstException;
import com.example.find_my_edge.analytics.engine.aggregate.AggregateComputeEngine;
import com.example.find_my_edge.analytics.engine.dataSet.GlobalTradeDataset;
import com.example.find_my_edge.analytics.engine.dataSet.TradeDataset;
import com.example.find_my_edge.analytics.model.ComputationContext;
import com.example.find_my_edge.analytics.service.AggregateExecutionService;
import com.example.find_my_edge.common.config.uiconfigs.AstConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class AggregateExecutionServiceImpl implements AggregateExecutionService {

    private final AggregateComputeEngine aggregateComputeEngine;
    private final ExecutorService executorService;

    @Override
    public <T> void executeAggregate(
            Collection<T> source,
            Function<T, String> keyFn,
            BiFunction<String, T, String> formulaFn,
            BiFunction<String, T, AstConfig> cfgFn,
            BiConsumer<String, Double> consumer,
            ComputationContext ctx
    ) {

        TradeDataset dataset = new GlobalTradeDataset(ctx);

        Map<String, Future<Double>> futures = new HashMap<>();

        for (T item : source) {

            String key = keyFn.apply(item);

            String formula = formulaFn.apply(key, item);
            AstConfig config = cfgFn.apply(key, item);

            futures.put(
                    key,
                    executorService.submit(
                            () ->
                                    aggregateComputeEngine.computedAggregate(
                                            config,
                                            formula,
                                            ctx.getSchemasById(),
                                            dataset
                                    )
                    )
            );
        }

        collectFutures(futures).forEach(consumer);
    }

    private Map<String, Double> collectFutures(Map<String, Future<Double>> futures) {
        Map<String, Double> result = new HashMap<>();

        for (Map.Entry<String, Future<Double>> entry : futures.entrySet()) {
            try {
                result.put(entry.getKey(), entry.getValue().get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new AstException("Thread interrupted", e);
            } catch (ExecutionException e) {
                throw new AstException(
                        "Error computing key: " + entry.getKey(),
                        e.getCause()
                );
            }
        }

        return result;
    }

}
