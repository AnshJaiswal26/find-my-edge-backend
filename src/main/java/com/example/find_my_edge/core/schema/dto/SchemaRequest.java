package com.example.find_my_edge.core.schema.dto;

import com.example.find_my_edge.core.schema.enums.FieldType;
import com.example.find_my_edge.core.schema.enums.SchemaSource;
import com.example.find_my_edge.core.schema.enums.SemanticType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchemaRequest {

    private String id;
    private String label;

    /* TYPE */
    @Builder.Default
    private FieldType type = FieldType.TEXT;

    @Builder.Default
    private SemanticType semanticType = SemanticType.STRING;

    /* COMPUTATION */
    @Builder.Default
    private String mode = "row";

    private ExpressionDto ast;

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
    private Long initialValue = 0L;

    /* DISPLAY */
    @Builder.Default
    private DisplayDto display = new DisplayDto("", 2);

    /* UI */
    @Builder.Default
    private List<ColorRuleDto> colorRules = new ArrayList<>();

    @Builder.Default
    private List<String> options = new ArrayList<>();

}
