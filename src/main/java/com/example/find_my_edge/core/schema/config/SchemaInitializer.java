package com.example.find_my_edge.core.schema.config;

import com.example.find_my_edge.common.dto.ColorRuleDTO;
import com.example.find_my_edge.common.dto.DisplayDTO;
import com.example.find_my_edge.common.dto.AstDTO;
import com.example.find_my_edge.core.schema.dto.SchemaRequest;
import com.example.find_my_edge.core.schema.enums.FieldType;
import com.example.find_my_edge.core.schema.enums.SemanticType;
import com.example.find_my_edge.core.schema.service.impl.SchemaServiceImpl;
import com.example.find_my_edge.core.schema.enums.SchemaSource;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class SchemaInitializer {

    private final SchemaServiceImpl schemaService;

    @PostConstruct
    public void init() {
        buildSystemSchemas().forEach(schemaService::seed);
    }

    private List<SchemaRequest> buildSystemSchemas() {
        return List.of(
                systemField("date", "Date", FieldType.DATE, SemanticType.DATE)
                        .display(display("YYYY-MM-DD", 0))
                        .build(),

                systemField("entryTime", "Entry Time", FieldType.TIME, SemanticType.TIME)
                        .display(display("hh:mm:ss A", 0))
                        .build(),

                systemField("exitTime", "Exit Time", FieldType.TIME, SemanticType.TIME)
                        .display(display("hh:mm:ss A", 0))
                        .build(),
                duration(),

                systemField("symbol", "Symbol", FieldType.TEXT, SemanticType.STRING)
                        .build(),

                systemField("entry", "Entry", FieldType.NUMBER, SemanticType.NUMBER)
                        .display(display("NUMBER", 2))
                        .build(),

                systemField("exit", "Exit", FieldType.NUMBER, SemanticType.NUMBER)
                        .display(display("NUMBER", 2))
                        .build(),

                systemField("qty", "Qty", FieldType.NUMBER, SemanticType.NUMBER)
                        .display(display("NUMBER", 0))
                        .build(),

                pnl(),
                riskReward(),
                SchemaRequest.builder()
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

    private SchemaRequest.SchemaRequestBuilder systemField(
            String id,
            String label,
            FieldType type,
            SemanticType semanticType
    ) {
        return SchemaRequest.builder()
                            .id(id)
                            .label(label)
                            .type(type)
                            .semanticType(semanticType)
                            .source(SchemaSource.SYSTEM)
                            .editable(true);
    }

    /* ---------------- COMPUTED ---------------- */

    private SchemaRequest duration() {
        return SchemaRequest.builder()
                            .id("duration")
                            .label("Duration")
                            .type(FieldType.DURATION)
                            .semanticType(SemanticType.DURATION)
                            .source(SchemaSource.COMPUTED)
                            .editable(false)
                            .dependencies(List.of("entryTime", "exitTime"))
                            .formula("[Exit Time] - [Entry Time]")
                            .ast(binary("-", key("exitTime"), key("entryTime")))
                            .display(display("HH:mm:ss", 0))
                            .colorRules(List.of(
                                    colorRule("lessThan", 5.0, "var(--warning)"),
                                    colorRule("greaterThan", 30.0, "var(--info)")
                            ))
                            .build();
    }

    private SchemaRequest pnl() {
        return SchemaRequest.builder()
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
                                            "*",
                                            binary("-", key("exit"), key("entry")),
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

    private SchemaRequest riskReward() {
        return SchemaRequest.builder()
                            .id("riskReward")
                            .label("Risk Reward")
                            .type(FieldType.NUMBER)
                            .semanticType(SemanticType.NUMBER)
                            .source(SchemaSource.COMPUTED)
                            .editable(false)
                            .dependencies(List.of("pnl"))
                            .formula("PnL / 500")
                            .ast(binary("/", key("pnl"), constant(500.0)))
                            .display(display("RATIO", 2))
                            .colorRules(List.of(
                                    colorRule("greaterThan", 0.0, "var(--success)"),
                                    colorRule("lessThan", 0.0, "var(--error)")
                            ))
                            .build();
    }

    /* ---------------- AST HELPERS ---------------- */

    private AstDTO key(String key) {
        return AstDTO.builder()
                     .type("key")
                     .key(key)
                     .build();
    }

    private AstDTO constant(Double value) {
        return AstDTO.builder()
                     .type("constant")
                     .value(value)
                     .build();
    }

    private AstDTO binary(String op, AstDTO left, AstDTO right) {
        return AstDTO.builder()
                     .type("binary")
                     .op(op)
                     .left(left)
                     .right(right)
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
