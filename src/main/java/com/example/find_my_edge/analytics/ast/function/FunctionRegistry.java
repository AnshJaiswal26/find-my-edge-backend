package com.example.find_my_edge.analytics.ast.function;

import com.example.find_my_edge.analytics.ast.function.enums.FunctionMode;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FunctionRegistry {

    private final Map<String, FunctionDefinition> registry = new HashMap<>();
    private final Map<FunctionMode, Set<String>> allowedByMode;

    public FunctionRegistry(
            List<Reducer> reducers,
            FunctionRegistryLoader loader
    ) {

        Map<String, FunctionMeta> defs = loader.getDefs();
        this.allowedByMode = loader.getAllowedByMode();

        for (Reducer reducer : reducers) {

            String name = reducer.getName().toUpperCase();

            FunctionMeta def = defs.get(name);

            if (def == null) {
                throw new IllegalStateException(
                        "Missing function definition in JSON for: " + name
                );
            }

            registry.put(name, new FunctionDefinition(name, reducer, def));

        }

    }

    public FunctionDefinition get(String name) {
        return registry.get(name.toUpperCase());
    }

    public Set<String> getAllowedFunctions(FunctionMode mode) {
        return allowedByMode.getOrDefault(mode, Set.of());
    }
}