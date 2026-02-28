package com.example.find_my_edge.common.config;

import com.example.find_my_edge.analytics.ast.enums.NodeType;
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

    private NodeType type;   // "binary"| "key" | "constant" | "function"

    // for binary
    private String op;     // "+", "-", "*", "/"

    private AstConfig left;
    private AstConfig right;

    // for key
    private String field;

    // for constant
    private Object value;
    private String valueType; // "string" | "number"

    // for function
    private String fn;
    private List<AstConfig> args;
    private AstConfig arg;
}
