package com.example.find_my_edge.domain.schema.model;


import com.example.find_my_edge.analytics.ast.util.HasDependencies;
import com.example.find_my_edge.common.config.AstConfig;
import com.example.find_my_edge.common.config.ColorRuleConfig;
import com.example.find_my_edge.common.config.DisplayConfig;
import com.example.find_my_edge.domain.schema.enums.*;
import com.example.find_my_edge.domain.schema.exception.SchemaValidationException;
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
public class Schema implements HasDependencies {

    private String id;
    private String label;

    @Builder.Default
    private Boolean hidden = false;

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

    @Builder.Default
    private SchemaRole role = SchemaRole.USER_DEFINED;

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

    /* ---------------- SOURCE HELPERS ---------------- */

    public boolean isSystem() {
        return source == SchemaSource.SYSTEM;
    }

    public boolean isUser() {
        return source == SchemaSource.USER;
    }

    public boolean isComputed() {
        return source == SchemaSource.COMPUTED;
    }

    /* ---------------- ROLE HELPERS ---------------- */

    public boolean isSystemRequired() {
        return role == SchemaRole.SYSTEM_REQUIRED;
    }

    public boolean isSystemOptional() {
        return role == SchemaRole.SYSTEM_OPTIONAL;
    }

    public boolean isUserDefined() {
        return role == SchemaRole.USER_DEFINED;
    }

    /* ---------------- BEHAVIOR HELPERS ---------------- */

    /**
     * Can user edit cell values?
     */
    public boolean isCellEditable() {
        // computed values are never editable
        return !isComputed();
    }

    /**
     * Can user modify schema (formula, dependencies, etc.)?
     */
    public boolean isSchemaEditable() {
        return isUserDefined();
    }

    /**
     * Can user delete this schema?
     */
    public boolean isDeletable() {
        return isUserDefined();
    }


    /**
     * Is this schema critical for system?
     */
    public boolean isSystemCritical() {
        return isSystemRequired();
    }

    /* ---------------- COMPUTATION HELPERS ---------------- */

    /**
     * Does this schema have a formula?
     */
    public boolean hasFormula() {
        return formula != null && !formula.isBlank();
    }

    /**
     * Is this actively computed (has AST or formula)?
     */
    public boolean isComputedField() {
        return isComputed() || hasFormula() || ast != null;
    }

    /**
     * Has dependencies?
     */
    public boolean hasDependencies() {
        return dependencies != null && !dependencies.isEmpty();
    }

    /* ---------------- UI HELPERS ---------------- */

    /**
     * Should this be visible in UI?
     */
    public boolean isVisible() {
        return hidden == null || !hidden;
    }

    /**
     * Is selectable field (dropdown type)?
     */
    public boolean hasOptions() {
        return options != null && !options.isEmpty();
    }

    public void validateForWrite() {

        validateSourceRoleCombination();

        if (isUserDefined() || isComputed()) {
            validateComputation();
            validateDependencies();
        }
    }

    private void validateSourceRoleCombination() {

        boolean valid = switch (source) {

            case SYSTEM -> role == SchemaRole.SYSTEM_REQUIRED
                           || role == SchemaRole.SYSTEM_OPTIONAL;

            case USER -> role == SchemaRole.USER_DEFINED;

            case COMPUTED -> role == SchemaRole.USER_DEFINED
                             || role == SchemaRole.SYSTEM_REQUIRED
                             || role == SchemaRole.SYSTEM_OPTIONAL;
        };

        if (!valid) {
            throw new SchemaValidationException(
                    "Invalid Schema combination: " + source + " + " + role
            );
        }
    }

    private void validateComputation() {

        if (isComputed() && !hasFormula() && ast == null) {
            throw new SchemaValidationException(
                    "Computed schema must have formula or AST"
            );
        }

        // Optional strict rule
        if (!isComputed() && hasFormula()) {
            System.out.println(isComputed() + source.toString());
            System.out.println(hasFormula());
            throw new SchemaValidationException(
                    "Non-computed schema cannot have formula"
            );
        }
    }

    private void validateDependencies() {

        if (hasDependencies() && !isComputed()) {
            throw new SchemaValidationException(
                    "Only computed schema can have dependencies"
            );
        }
    }
}
