package com.example.find_my_edge.common.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AstConfig {

    private String type;   // "binary"| "key" | "constant" | "function"

    // for binary
    private String op;     // "+", "-", "*", "/"

    private AstConfig left;
    private AstConfig right;

    // for key
    private String key;

    // for constant
    private Double value;
    private String valueType; // "string" | "number"

    // for function
    private String fn;
    private List<AstConfig> args;
    private AstConfig arg;
}
