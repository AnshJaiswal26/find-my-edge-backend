package com.example.find_my_edge.analytics.ast.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class AstResult {
    private AstNode astNode;
    private Set<String> dependencies;
}
