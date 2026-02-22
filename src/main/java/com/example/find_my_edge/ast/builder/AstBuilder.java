package com.example.find_my_edge.ast.builder;

import com.example.find_my_edge.common.dto.AstDTO;

import java.util.Arrays;
import java.util.List;

public class AstBuilder {

    public static AstDTO key(String key) {
        return AstDTO.builder()
                     .type("key")
                     .key(key)
                     .build();
    }

    public static AstDTO constant(Double value) {
        return AstDTO.builder()
                     .type("constant")
                     .value(value)
                     .build();
    }

    public static AstDTO binary(String op, AstDTO left, AstDTO right) {
        return AstDTO.builder()
                     .type("binary")
                     .op(op)
                     .left(left)
                     .right(right)
                     .build();
    }

    public static AstDTO function(String fn, List<AstDTO> args) {
        return AstDTO.builder()
                     .type("function")
                     .fn(fn)
                     .args(args)
                     .build();
    }

    public static AstDTO function(String fn, AstDTO... args) {
        return function(fn, Arrays.asList(args));
    }
}