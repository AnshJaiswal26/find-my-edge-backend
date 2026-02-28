package com.example.find_my_edge.analytics.ast.model;

import com.example.find_my_edge.analytics.ast.enums.NodeType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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

    private NodeType type;

    // for binary
    private String op;       // "+", "-", "*", "/", etc.
    private AstNode left;
    private AstNode right;

    // for unary
    private AstNode arg;

    // for key
    private String field;

    // for constant
    private Object value;
    private String valueType; // "string" | "number"

    // for function
    private String fn;
    private List<AstNode> args;

    /* ---------- Helper Methods (Optional but Recommended) ---------- */

    public boolean isBinary() {
        return this.type == NodeType.BINARY;
    }

    public boolean isUnary() {
        return this.type == NodeType.UNARY;
    }

    public boolean isFunction() {
        return this.type == NodeType.FUNCTION;
    }

    public boolean isField() {
        return this.type == NodeType.IDENTIFIER;
    }

    public boolean isConstant() {
        return this.type == NodeType.CONSTANT;
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);

        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "AstNode{error_serializing=" + e.getMessage() + "}";
        }
    }
}