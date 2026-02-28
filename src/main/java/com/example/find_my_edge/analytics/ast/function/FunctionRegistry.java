package com.example.find_my_edge.analytics.ast.function;

import com.example.find_my_edge.analytics.ast.function.annotation.FunctionMeta;
import com.example.find_my_edge.analytics.ast.function.enums.FunctionMode;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
public class FunctionRegistry {

    private final Map<String, FunctionDefinition> registry = new HashMap<>();

    private final Map<FunctionMode, Set<String>> allowedByMode = new HashMap<>();

    public FunctionRegistry(List<Reducer> reducers) {

        for (Reducer reducer : reducers) {

            FunctionMeta meta = reducer.getClass().getAnnotation(FunctionMeta.class);

            if (meta == null) {
                throw new IllegalStateException(
                        "Missing @FunctionMeta on " + reducer.getClass().getSimpleName()
                );
            }

            String name = reducer.getName().toUpperCase();

            registry.put(name, new FunctionDefinition(name, reducer, meta));

            for (FunctionMode mode : meta.modes()) {
                allowedByMode
                        .computeIfAbsent(mode, k -> new HashSet<>())
                        .add(name);
            }
        }

        allowedByMode
                .computeIfAbsent(FunctionMode.BASE, k -> new HashSet<>())
                .add("COUNT_ALL");

        allowedByMode
                .computeIfAbsent(FunctionMode.WINDOW, k -> new HashSet<>())
                .add("COUNT_ALL");
    }

    public FunctionDefinition get(String name) {
        return registry.get(name.toUpperCase());
    }

    public Set<String> getAllowedFunctions(FunctionMode mode) {
        return allowedByMode.getOrDefault(mode, Set.of());
    }
}