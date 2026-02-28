package com.example.find_my_edge.analytics.ast.model;

import com.example.find_my_edge.analytics.ast.enums.ValueType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class AstResult {

    public AstResult(AstNode node, Set<String> dependencies) {
        this.astNode = node;
        this.dependencies = dependencies;
        this.semanticType = null;
    }

    private AstNode astNode;
    private Set<String> dependencies;
    private ValueType semanticType;
}
