package com.example.find_my_edge.analytics.execution.impl;

import com.example.find_my_edge.analytics.ast.exception.AstException;
import com.example.find_my_edge.analytics.execution.GroupSeriesExecutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class GroupSeriesExecutionServiceImpl implements GroupSeriesExecutionService {

    private final ExecutorService executorService;

    @Override
    public <G, S> Map<String, Map<String, Double>> execute(

            Collection<G> groups,
            Collection<S> series,

            Function<G, String> groupKeyFn,
            Function<S, String> seriesKeyFn,

            BiFunction<G, S, Double> computeFn
    ) {

        Map<String, Map<String, Future<Double>>> futures = new HashMap<>();

        for (G group : groups) {

            String gKey = groupKeyFn.apply(group);

            futures.putIfAbsent(gKey, new HashMap<>());

            for (S s : series) {

                String sKey = seriesKeyFn.apply(s);

                futures.get(gKey).put(
                        sKey,
                        executorService.submit(() -> computeFn.apply(group, s))
                );
            }
        }

        return collect(futures);
    }

    private Map<String, Map<String, Double>> collect(
            Map<String, Map<String, Future<Double>>> futures
    ) {

        Map<String, Map<String, Double>> result = new HashMap<>();

        futures.forEach((gKey, seriesMap) -> {

            Map<String, Double> seriesResult = new HashMap<>();

            seriesMap.forEach((sKey, future) -> {

                try {
                    seriesResult.put(sKey, future.get());
                } catch (Exception e) {
                    throw new AstException("Error computing: " + gKey + ":" + sKey, e);
                }

            });

            result.put(gKey, seriesResult);

        });

        return result;
    }
}