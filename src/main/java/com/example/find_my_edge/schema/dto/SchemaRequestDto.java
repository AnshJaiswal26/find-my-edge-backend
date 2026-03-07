package com.example.find_my_edge.schema.dto;

import com.example.find_my_edge.common.config.uiconfigs.AstConfig;
import com.example.find_my_edge.common.config.uiconfigs.ColorRuleConfig;
import com.example.find_my_edge.common.config.uiconfigs.DisplayConfig;
import com.example.find_my_edge.schema.enums.*;
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
    private String idFormula;

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