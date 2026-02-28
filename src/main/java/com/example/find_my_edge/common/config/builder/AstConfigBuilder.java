package com.example.find_my_edge.common.config.builder;

import com.example.find_my_edge.analytics.ast.enums.NodeType;
import com.example.find_my_edge.common.config.AstConfig;

import java.util.Arrays;
import java.util.List;

public class AstConfigBuilder {
    private AstConfigBuilder() {
    }

    public static AstConfig field(String field) {
        return AstConfig.builder()
                        .type(NodeType.IDENTIFIER)
                        .field(field)
                        .build();
    }

    public static AstConfig constant(Double value) {
        return AstConfig.builder()
                        .type(NodeType.CONSTANT)
                        .value(value)
                        .valueType("number")
                        .build();
    }

    public static AstConfig constant(String value) {
        return AstConfig.builder()
                        .type(NodeType.CONSTANT)
                        .value(value)
                        .valueType("string")
                        .build();
    }

    public static AstConfig binary(AstConfig left, String op, AstConfig right) {
        return AstConfig.builder()
                        .type(NodeType.BINARY)
                        .op(op)
                        .left(left)
                        .right(right)
                        .build();
    }

    public static AstConfig function(String fn, List<AstConfig> args) {
        return AstConfig.builder()
                        .type(NodeType.FUNCTION)
                        .fn(fn)
                        .args(args)
                        .build();
    }

    public static AstConfig function(String fn, AstConfig... args) {
        return function(fn, Arrays.asList(args));
    }
}