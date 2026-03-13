package com.example.find_my_edge.analytics.execution;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface GroupSeriesExecutionService {
    <G, S> Map<String, Map<String, Double>> execute(

            Collection<G> groups,
            Collection<S> series,

            Function<G, String> groupKeyFn,
            Function<S, String> seriesKeyFn,

            BiFunction<G, S, Double> computeFn
    );
}
