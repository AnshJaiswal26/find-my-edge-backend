package com.example.find_my_edge.api.schema.dto;

import com.example.find_my_edge.common.config.AstConfig;
import com.example.find_my_edge.common.config.ColorRuleConfig;
import com.example.find_my_edge.common.config.DisplayConfig;
import com.example.find_my_edge.domain.schema.enums.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchemaRequestDto {

    private String label;

    /* TYPE */
    private FieldType type;
    private SemanticType semanticType;

    /* COMPUTATION */
    private ComputeMode mode;
    private AstConfig ast;
    private String formula;
    private List<String> dependencies;

    private SchemaSource source;
    private SchemaRole role;

    private Double initialValue;
    private Boolean hidden;

    /* DISPLAY */
    private DisplayConfig display;

    /* UI */
    private List<ColorRuleConfig> colorRules;
    private List<String> options;
}