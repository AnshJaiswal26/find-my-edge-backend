package com.example.find_my_edge.ast.context;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SchemaType {
    private String format;
    private String semanticType;
}