package com.example.find_my_edge.core.schema.registry;

import com.example.find_my_edge.common.dto.ColorRuleDTO;
import com.example.find_my_edge.common.dto.DisplayDTO;

import com.example.find_my_edge.core.schema.dto.SchemaResponseDTO;
import com.example.find_my_edge.core.schema.enums.FieldType;
import com.example.find_my_edge.core.schema.enums.SemanticType;
import com.example.find_my_edge.core.schema.enums.SchemaSource;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.example.find_my_edge.ast.builder.AstBuilder.*;

@Component
@RequiredArgsConstructor
public class SystemSchemaRegistry {

    private Map<String, SchemaResponseDTO> schemas;
    private Set<String> schemasOrder;

    @PostConstruct
    public void init() {
        Map<String, SchemaResponseDTO> tempMap = new HashMap<>();
        Set<String> tempOrder = new LinkedHashSet<>();

        buildSystemSchemas().forEach(schema -> {
            tempOrder.add(schema.getId());
            tempMap.put(schema.getId(), schema);
        });

        this.schemas = Map.copyOf(tempMap);
        this.schemasOrder = Set.copyOf(tempOrder);
    }

    public List<SchemaResponseDTO> getAll() {
        return schemasOrder.stream()
                           .map(schemas::get)
                           .toList();
    }

    public SchemaResponseDTO get(String id) {
        return schemas.get(id);
    }

    public Set<String> getOrder() {
        return schemasOrder;
    }

    public boolean exists(String id) {
        return schemasOrder.contains(id);
    }

    private List<SchemaResponseDTO> buildSystemSchemas() {
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
                SchemaResponseDTO.builder()
                                 .id("emotion")
                                 .label("Emotion")
                                 .type(FieldType.SELECT)
                                 .semanticType(SemanticType.STRING)
                                 .source(SchemaSource.SYSTEM)
                                 .editable(true)
                                 .options(List.of("Calm", "Fear", "Greed"))
                                 .display(display("badge", null))
                                 .build()
        );
    }

    /* ---------------- SYSTEM FIELD BUILDER ---------------- */

    private SchemaResponseDTO.SchemaResponseDTOBuilder systemField(
            String id,
            String label,
            FieldType type,
            SemanticType semanticType
    ) {
        return SchemaResponseDTO.builder()
                                .id(id)
                                .label(label)
                                .type(type)
                                .semanticType(semanticType)
                                .source(SchemaSource.SYSTEM)
                                .editable(true);
    }

    /* ---------------- COMPUTED ---------------- */

    private SchemaResponseDTO duration() {
        return SchemaResponseDTO.builder()
                                .id("duration")
                                .label("Duration")
                                .type(FieldType.DURATION)
                                .semanticType(SemanticType.DURATION)
                                .source(SchemaSource.COMPUTED)
                                .editable(false)
                                .dependencies(List.of("entryTime", "exitTime"))
                                .formula("[Exit Time] - [Entry Time]")
                                .ast(binary(key("exitTime"), "-", key("entryTime")))
                                .display(display("HH:mm:ss", 0))
                                .colorRules(List.of(
                                        colorRule("lessThan", 5.0, "var(--warning)"),
                                        colorRule("greaterThan", 30.0, "var(--info)")
                                ))
                                .build();
    }

    private SchemaResponseDTO pnl() {
        return SchemaResponseDTO.builder()
                                .id("pnl")
                                .label("PnL")
                                .type(FieldType.NUMBER)
                                .semanticType(SemanticType.NUMBER)
                                .source(SchemaSource.COMPUTED)
                                .editable(false)
                                .dependencies(List.of("exit", "entry", "qty"))
                                .formula("(Exit - Entry) * Qty")
                                .ast(
                                        binary(
                                                binary(key("exit"), "-", key("entry")),
                                                "*",
                                                key("qty")
                                        )
                                )
                                .display(display("CURRENCY", 2))
                                .colorRules(List.of(
                                        colorRule("greaterThan", 0.0, "var(--success)"),
                                        colorRule("lessThan", 0.0, "var(--error)")
                                ))
                                .build();
    }

    private SchemaResponseDTO riskReward() {
        return SchemaResponseDTO.builder()
                                .id("riskReward")
                                .label("Risk Reward")
                                .type(FieldType.NUMBER)
                                .semanticType(SemanticType.NUMBER)
                                .source(SchemaSource.COMPUTED)
                                .editable(false)
                                .dependencies(List.of("pnl"))
                                .formula("PnL / 500")
                                .ast(binary(key("pnl"), "/", constant(500.0)))
                                .display(display("RATIO", 2))
                                .colorRules(List.of(
                                        colorRule("greaterThan", 0.0, "var(--success)"),
                                        colorRule("lessThan", 0.0, "var(--error)")
                                ))
                                .build();
    }

    /* ---------------- UI HELPERS ---------------- */

    private DisplayDTO display(String format, Integer decimals) {
        return DisplayDTO.builder()
                         .format(format)
                         .decimals(decimals != null ? decimals : 2)
                         .build();
    }

    private ColorRuleDTO colorRule(String operator, Double value, String color) {
        return ColorRuleDTO.builder()
                           .operator(operator)
                           .value(value)
                           .color(color)
                           .build();
    }
}
