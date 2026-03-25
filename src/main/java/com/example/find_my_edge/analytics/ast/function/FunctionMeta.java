package com.example.find_my_edge.analytics.ast.function;

import com.example.find_my_edge.analytics.ast.function.enums.ExecutionMode;
import com.example.find_my_edge.analytics.ast.function.enums.FunctionType;
import com.example.find_my_edge.analytics.ast.function.enums.WindowStrategy;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class FunctionMeta {

    public List<Object> args; // String OR List<String>
    public String returnType;
    public Map<String, List<String>> generics;

    public String signature;
    public String description;

    public FunctionType type;
    public ExecutionMode executionMode;
    private WindowStrategy strategy;
}