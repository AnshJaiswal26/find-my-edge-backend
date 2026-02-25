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

    private NodeType type;   // 👈 enum instead of String

    // for binary
    private String op;       // "+", "-", "*", "/", etc.
    private AstNode left;
    private AstNode right;

    // for unary
    private AstNode arg;

    // for key
    private String key;

    // for constant
    private Double value;
    private String valueType; // "string" | "number"

    // for function
    private String fn;
    private List<AstNode> args;

    /* ---------- ENUM ---------- */
    public enum NodeType {
        BINARY,
        UNARY,
        KEY,
        CONSTANT,
        FUNCTION
    }

    /* ---------- Helper Methods (Optional but Recommended) ---------- */

    public boolean isBinary() {
        return type == NodeType.BINARY;
    }

    public boolean isUnary() {
        return type == NodeType.UNARY;
    }

    public boolean isFunction() {
        return type == NodeType.FUNCTION;
    }

    public boolean isKey() {
        return type == NodeType.KEY;
    }

    public boolean isConstant() {
        return type == NodeType.CONSTANT;
    }
}