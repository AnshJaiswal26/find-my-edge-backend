package com.example.find_my_edge.schema.registry;

import com.example.find_my_edge.common.config.uiconfigs.ColorRuleConfig;
import com.example.find_my_edge.common.config.uiconfigs.DisplayConfig;

import com.example.find_my_edge.schema.enums.FieldType;
import com.example.find_my_edge.schema.enums.SchemaRole;
import com.example.find_my_edge.schema.enums.SemanticType;
import com.example.find_my_edge.schema.enums.SchemaSource;

import com.example.find_my_edge.schema.model.Schema;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.example.find_my_edge.common.builder.AstConfigBuilder.*;


@Component
@RequiredArgsConstructor
public class SchemaRegistry {

    private Map<String, Schema> schemas;
    private Set<String> schemasOrder;

    @PostConstruct
    public void init() {
        Map<String, Schema> tempMap = new HashMap<>();
        Set<String> tempOrder = new LinkedHashSet<>();

        buildSystemSchemas().forEach(schema -> {
            tempOrder.add(schema.getId());
            tempMap.put(schema.getId(), schema);
        });

        this.schemas = Map.copyOf(tempMap);
        this.schemasOrder = Collections.unmodifiableSet(new LinkedHashSet<>(tempOrder));
    }

    public List<Schema> getAll() {
        return schemasOrder.stream()
                           .map(schemas::get)
                           .toList();
    }

    public Schema get(String id) {
        return schemas.get(id);
    }

    public Set<String> getOrder() {
        return schemasOrder;
    }

    public boolean exists(String id) {
        return schemasOrder.contains(id);
    }

    private List<Schema> buildSystemSchemas() {

        return List.of(
                systemField(
                        "date",
                        "Date",
                        FieldType.DATE,
                        SemanticType.DATE,
                        display("YYYY-MM-DD", 0)
                ),

                systemField(
                        "entryTime",
                        "Entry Time",
                        FieldType.TIME,
                        SemanticType.TIME,
                        display("hh:mm:ss A", 0)
                ),

                systemField(
                        "exitTime",
                        "Exit Time",
                        FieldType.TIME,
                        SemanticType.TIME,
                        display("hh:mm:ss A", 0)
                ),

                duration(),

                systemField(
                        "symbol",
                        "Symbol",
                        FieldType.TEXT,
                        SemanticType.STRING,
                        display("", 2)
                ),

                Schema.builder()
                      .id("direction")
                      .label("Direction")
                      .type(FieldType.TEXT)
                      .semanticType(SemanticType.STRING)
                      .source(SchemaSource.SYSTEM)
                      .role(SchemaRole.SYSTEM_OPTIONAL)
                      .colorRules(List.of(
                              colorRule("lessThan", "CALL", "var(--success)"),
                              colorRule("greaterThan", "PUT", "var(--error)")
                      ))
                      .build(),

                systemField(
                        "entryPrice",
                        "Entry Price",
                        FieldType.NUMBER,
                        SemanticType.NUMBER,
                        display("NUMBER", 2)
                ),

                systemField(
                        "exitPrice",
                        "Exit Price",
                        FieldType.NUMBER,
                        SemanticType.NUMBER,
                        display("NUMBER", 2)
                ),

                systemField(
                        "qty",
                        "Qty",
                        FieldType.NUMBER,
                        SemanticType.NUMBER,
                        display("NUMBER", 0)
                ),

                pnl(),

                Schema.builder()
                      .id("charges")
                      .label("Charges")
                      .type(FieldType.NUMBER)
                      .semanticType(SemanticType.NUMBER)
                      .source(SchemaSource.SYSTEM)
                      .role(SchemaRole.SYSTEM_OPTIONAL)
                      .display(display("CURRENCY", 2))
                      .build()

//                Schema.builder()
//                      .id("emotion")
//                      .label("Emotion")
//                      .type(FieldType.SELECT)
//                      .semanticType(SemanticType.STRING)
//                      .source(SchemaSource.SYSTEM)
//                      .role(SchemaRole.SYSTEM_OPTIONAL)
//                      .options(List.of("Calm", "Fear", "Greed"))
//                      .display(display("badge", null))
//                      .build()
        );
    }

    /* ---------------- SYSTEM FIELD BUILDER ---------------- */

    private Schema systemField(
            String id,
            String label,
            FieldType type,
            SemanticType semanticType,
            DisplayConfig displayConfig
    ) {
        return Schema.builder()
                     .id(id)
                     .label(label)
                     .type(type)
                     .semanticType(semanticType)
                     .source(SchemaSource.SYSTEM)
                     .role(SchemaRole.SYSTEM_REQUIRED)
                     .display(displayConfig)
                     .build();
    }

    /* ---------------- COMPUTED ---------------- */

    private Schema duration() {
        return Schema.builder()
                     .id("duration")
                     .label("Duration")
                     .type(FieldType.DURATION)
                     .semanticType(SemanticType.DURATION)
                     .source(SchemaSource.COMPUTED)
                     .role(SchemaRole.SYSTEM_REQUIRED)
                     .dependencies(List.of("entryTime", "exitTime"))
                     .formula("[Exit Time] - [Entry Time]")
                     .idFormula("@{exitTime} - @{entryTime}")
                     .ast(binary(field("exitTime"), "-", field("entryTime")))
                     .display(display("HH:mm:ss", 0))
                     .colorRules(List.of(
                             colorRule("lessThan", 5.0, "var(--warning)"),
                             colorRule("greaterThan", 30.0, "var(--info)")
                     ))
                     .build();
    }

    private Schema pnl() {
        return Schema.builder()
                     .id("pnl")
                     .label("PnL")
                     .type(FieldType.NUMBER)
                     .semanticType(SemanticType.NUMBER)
                     .source(SchemaSource.COMPUTED)
                     .role(SchemaRole.SYSTEM_REQUIRED)
                     .dependencies(List.of("exitPrice", "entryPrice", "qty"))
                     .formula("([Exit Price] - [Entry Price]) * [Qty]")
                     .idFormula("(@{exitPrice} - @{entryPrice}) * @{qty}")
                     .ast(
                             binary(
                                     binary(field("exitPrice"), "-", field("entryPrice")),
                                     "*",
                                     field("qty")
                             )
                     )
                     .display(display("CURRENCY", 2))
                     .colorRules(List.of(
                             colorRule("greaterThan", 0.0, "var(--success)"),
                             colorRule("lessThan", 0.0, "var(--error)")
                     ))
                     .build();
    }

    private Schema isProfit() {
        return Schema.builder()
                     .id("isProfit")
                     .label("isProfit")
                     .type(FieldType.BOOLEAN)
                     .semanticType(SemanticType.BOOLEAN)
                     .source(SchemaSource.COMPUTED)
                     .role(SchemaRole.SYSTEM_REQUIRED)
                     .dependencies(List.of("pnl"))
                     .formula("[pnl] > 0")
                     .ast(
                             binary(
                                     field("pnl"),
                                     ">",
                                     constant(0.0)
                             )
                     )
                     .display(display("YES_NO", 2))
                     .build();
    }

    private Schema riskReward() {
        return Schema.builder()
                     .id("riskReward")
                     .label("Risk Reward")
                     .type(FieldType.NUMBER)
                     .semanticType(SemanticType.NUMBER)
                     .source(SchemaSource.COMPUTED)
                     .role(SchemaRole.SYSTEM_REQUIRED)
                     .dependencies(List.of("pnl"))
                     .formula("[PnL] / 500")
                     .ast(binary(field("pnl"), "/", constant(500.0)))
                     .display(display("RATIO", 2))
                     .colorRules(List.of(
                             colorRule("greaterThan", 0.0, "var(--success)"),
                             colorRule("lessThan", 0.0, "var(--error)")
                     ))
                     .build();
    }

    /* ---------------- UI HELPERS ---------------- */

    private DisplayConfig display(String format, Integer decimals) {
        return DisplayConfig.builder()
                            .format(format)
                            .decimals(decimals != null ? decimals : 2)
                            .build();
    }

    private ColorRuleConfig colorRule(String operator, Object value, String color) {
        return ColorRuleConfig.builder()
                              .operator(operator)
                              .value(value)
                              .color(color)
                              .build();
    }
}
