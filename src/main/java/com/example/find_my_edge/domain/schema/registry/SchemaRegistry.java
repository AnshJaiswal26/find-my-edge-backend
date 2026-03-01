package com.example.find_my_edge.domain.schema.registry;

import com.example.find_my_edge.common.config.ColorRuleConfig;
import com.example.find_my_edge.common.config.DisplayConfig;

import static com.example.find_my_edge.common.config.builder.AstConfigBuilder.*;

import com.example.find_my_edge.domain.schema.enums.FieldType;
import com.example.find_my_edge.domain.schema.enums.SchemaRole;
import com.example.find_my_edge.domain.schema.enums.SemanticType;
import com.example.find_my_edge.domain.schema.enums.SchemaSource;

import com.example.find_my_edge.domain.schema.model.Schema;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;


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
        this.schemasOrder = Set.copyOf(tempOrder);
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
                        SemanticType.DATE
                )
                        .display(display("YYYY-MM-DD", 0))
                        .build(),

                systemField(
                        "entryTime",
                        "Entry Time",
                        FieldType.TIME,
                        SemanticType.TIME
                )
                        .display(display("hh:mm:ss A", 0))
                        .build(),

                systemField(
                        "exitTime",
                        "Exit Time",
                        FieldType.TIME,
                        SemanticType.TIME
                )
                        .display(display("hh:mm:ss A", 0))
                        .build(),

                duration(),

                systemField(
                        "symbol",
                        "Symbol",
                        FieldType.TEXT,
                        SemanticType.STRING
                )
                        .build(),

                systemField(
                        "entry",
                        "Entry",
                        FieldType.NUMBER,
                        SemanticType.NUMBER
                )
                        .display(display("NUMBER", 2))
                        .build(),

                systemField(
                        "exit",
                        "Exit",
                        FieldType.NUMBER,
                        SemanticType.NUMBER
                )
                        .display(display("NUMBER", 2))
                        .build(),

                systemField(
                        "qty",
                        "Qty",
                        FieldType.NUMBER,
                        SemanticType.NUMBER
                )
                        .display(display("NUMBER", 0))
                        .build(),

                pnl(),
                riskReward(),
                isProfit(),

                Schema.builder()
                      .id("emotion")
                      .label("Emotion")
                      .type(FieldType.SELECT)
                      .semanticType(SemanticType.STRING)
                      .source(SchemaSource.SYSTEM)
                      .role(SchemaRole.SYSTEM_OPTIONAL)
                      .options(List.of("Calm", "Fear", "Greed"))
                      .display(display("badge", null))
                      .build()
        );
    }

    /* ---------------- SYSTEM FIELD BUILDER ---------------- */

    private Schema.SchemaBuilder systemField(
            String id,
            String label,
            FieldType type,
            SemanticType semanticType
    ) {
        return Schema.builder()
                     .id(id)
                     .label(label)
                     .type(type)
                     .semanticType(semanticType)
                     .source(SchemaSource.SYSTEM)
                     .role(SchemaRole.SYSTEM_REQUIRED);
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
                     .dependencies(List.of("exit", "entry", "qty"))
                     .formula("([Exit] - [Entry]) * [Qty]")
                     .ast(
                             binary(
                                     binary(field("exit"), "-", field("entry")),
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

    private ColorRuleConfig colorRule(String operator, Double value, String color) {
        return ColorRuleConfig.builder()
                              .operator(operator)
                              .value(value)
                              .color(color)
                              .build();
    }
}
