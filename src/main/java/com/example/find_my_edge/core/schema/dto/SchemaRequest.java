package com.example.find_my_edge.core.schema.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class SchemaRequest {

    private UUID id;                 // null when creating new
    private String key;              // "pnl", "duration"
    private String label;            // Display name

    private String mode;
    private String type;

//    private SchemaSource source;     // SYSTEM | USER | COMPUTED
//    private ComputeType computeType; // ROW | WINDOW | GLOBAL | GROUP
//
//    private ValueType valueType;     // NUMBER | TEXT | TIME | DATE | RATIO | CURRENCY

    private Boolean editable;
    private Long initialValue;

    private ExpressionDto expression;     // Only for COMPUTED
    private String formula;               // Human-readable version

    private List<String> dependencies;    // ["entry", "exit"]

    private DisplayDto display;

    private List<ColorRuleDto> colorRules;
    private List<String> options;      // For select type
}
