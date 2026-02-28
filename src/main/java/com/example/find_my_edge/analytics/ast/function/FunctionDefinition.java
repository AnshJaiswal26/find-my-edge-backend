package com.example.find_my_edge.analytics.ast.function;

import com.example.find_my_edge.analytics.ast.function.annotation.FunctionMeta;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import lombok.Getter;

@Getter
public class FunctionDefinition {
    private final String name;
    private final Reducer reducer;
    private final FunctionMeta meta;

    public FunctionDefinition(String name, Reducer reducer, FunctionMeta meta) {
        this.name = name;
        this.reducer = reducer;
        this.meta = meta;
    }
}