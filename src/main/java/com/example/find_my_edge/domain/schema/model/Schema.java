package com.example.find_my_edge.domain.schema.model;


import com.example.find_my_edge.analytics.ast.model.AstNode;
import com.example.find_my_edge.common.config.AstConfig;
import com.example.find_my_edge.common.config.ColorRuleConfig;
import com.example.find_my_edge.common.config.DisplayConfig;
import com.example.find_my_edge.domain.schema.enums.ComputeMode;
import com.example.find_my_edge.domain.schema.enums.FieldType;
import com.example.find_my_edge.domain.schema.enums.SchemaSource;
import com.example.find_my_edge.domain.schema.enums.SemanticType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Schema {

    private String id;
    private String label;

    /* TYPE */
    @Builder.Default
    private FieldType type = FieldType.TEXT;

    @Builder.Default
    private SemanticType semanticType = SemanticType.STRING;

    /* COMPUTATION */
    @Builder.Default
    private ComputeMode mode = ComputeMode.ROW;

    private AstConfig ast;

    @Builder.Default
    private String formula = "";

    @Builder.Default
    private List<String> dependencies = new ArrayList<>();

    /* SOURCE */
    @Builder.Default
    private SchemaSource source = SchemaSource.USER;

    /* BEHAVIOR */
    @Builder.Default
    private Boolean editable = false;

    @Builder.Default
    private Double initialValue = 0.0;

    /* DISPLAY */
    @Builder.Default
    private DisplayConfig display = new DisplayConfig("", 2);

    /* UI */
    @Builder.Default
    private List<ColorRuleConfig> colorRules = new ArrayList<>();

    @Builder.Default
    private List<String> options = new ArrayList<>();
}
