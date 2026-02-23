package com.example.find_my_edge.core.schema.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "schema_overrides",
        uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "schemaId"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchemaOverrideEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String schemaId; // system schema reference

    @Column(columnDefinition = "TEXT")
    private String displayJson;

    @Column(columnDefinition = "TEXT")
    private String colorRulesJson;

}