package com.example.find_my_edge.schema.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

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
    private UUID userId;

    @Column(nullable = false)
    private String schemaId; // system schema reference

    private Boolean hidden;

    @Column(columnDefinition = "TEXT")
    private String displayJson;

    @Column(columnDefinition = "TEXT")
    private String colorRulesJson;

}