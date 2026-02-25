package com.example.find_my_edge.analytics.ast.reducer.runner;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ReducerRunnerRegistry {

    private final Map<String, ReducerRunnerStrategy> runners;

    public ReducerRunnerRegistry(Map<String, ReducerRunnerStrategy> runners) {
        this.runners = runners;
    }

    public ReducerRunnerStrategy get(String type) {
        return runners.get(type);
    }
}