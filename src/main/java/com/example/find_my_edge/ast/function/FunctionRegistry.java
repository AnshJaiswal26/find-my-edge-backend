package com.example.find_my_edge.ast.function;

import com.example.find_my_edge.ast.reducer.Reducer;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor
public class FunctionRegistry {

    private final Map<String, Reducer> registry = new HashMap<>();


    public FunctionRegistry(List<Reducer> reducers) {
        registry.putAll(reducers.stream()
                                .collect(
                                        Collectors.toMap(Reducer::getName, r -> r)
                                ));
    }

    public Reducer get(String name) {
        return registry.get(name.toUpperCase());
    }
}