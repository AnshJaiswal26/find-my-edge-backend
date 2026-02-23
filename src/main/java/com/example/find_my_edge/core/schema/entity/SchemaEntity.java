package com.example.find_my_edge.core.schema.entity;

import com.example.find_my_edge.core.schema.enums.ComputeMode;
import com.example.find_my_edge.core.schema.enums.FieldType;
import com.example.find_my_edge.core.schema.enums.SchemaSource;
import com.example.find_my_edge.core.schema.enums.SemanticType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "schemas",
        uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "schemaId", "label"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchemaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String label;

    @Column(nullable = false)
    private String userId;

    /* TYPE */
    @Enumerated(EnumType.STRING)
    private FieldType type = FieldType.TEXT;

    @Enumerated(EnumType.STRING)
    private SemanticType semanticType = SemanticType.STRING;

    /* COMPUTATION */
    @Enumerated(EnumType.STRING)
    private ComputeMode mode = ComputeMode.ROW;

    @Lob
    private String astJson; // store AstDTO as JSON

    private String formula = "";

    @ElementCollection
    @CollectionTable(
            name = "schema_dependencies",
            joinColumns = @JoinColumn(name = "schema_id"),
            indexes = {
                    @Index(name = "idx_dependency", columnList = "dependency")
            }
    )
    @Column(name = "dependency")
    private List<String> dependencies = new ArrayList<>();

    /* SOURCE */
    @Enumerated(EnumType.STRING)
    private SchemaSource source = SchemaSource.USER;

    /* BEHAVIOR */
    private Boolean editable = false;

    private Double initialValue = 0.0;

    /* DISPLAY */
    @Column(columnDefinition = "TEXT")
    private String displayJson; // store DisplayDTO as JSON

    /* UI */
    @Column(columnDefinition = "TEXT")
    private String colorRulesJson; // store List<ColorRuleDTO> as JSON

    @Column(columnDefinition = "TEXT")
    private String optionsJson; // store List<String> as JSON
}