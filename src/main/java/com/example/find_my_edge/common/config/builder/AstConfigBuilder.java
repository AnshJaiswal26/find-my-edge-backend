package com.example.find_my_edge.common.config.builder;


import com.example.find_my_edge.common.config.AstConfig;

import java.util.Arrays;
import java.util.List;

public class AstConfigBuilder {
    private AstConfigBuilder() {
    }

    public static AstConfig key(String key) {
        return AstConfig.builder()
                     .type("key")
                     .key(key)
                     .build();
    }

    public static AstConfig constant(Double value) {
        return AstConfig.builder()
                     .type("constant")
                     .value(value)
                     .build();
    }

    public static AstConfig binary(AstConfig left, String op, AstConfig right) {
        return AstConfig.builder()
                     .type("binary")
                     .op(op)
                     .left(left)
                     .right(right)
                     .build();
    }

    public static AstConfig function(String fn, List<AstConfig> args) {
        return AstConfig.builder()
                     .type("function")
                     .fn(fn)
                     .args(args)
                     .build();
    }

    public static AstConfig function(String fn, AstConfig... args) {
        return function(fn, Arrays.asList(args));
    }
}