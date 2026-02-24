package com.example.find_my_edge.analytics.ast.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AstNode {
    private String type;   // "binary"| "key" | "constant" | "function"

    // for binary
    private String op;     // "+", "-", "*", "/"

    private AstNode left;
    private AstNode right;

    // for key
    private String key;

    // for constant
    private Double value;
    private String valueType; // "string" | "number"

    // for function
    private String fn;
    private List<AstNode> args;
    private AstNode arg;
}
