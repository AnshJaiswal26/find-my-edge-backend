package com.example.find_my_edge.schema.entity;

import com.example.find_my_edge.schema.enums.ViewType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(
        name = "schema_order",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"userId", "viewType"})
        }
)
@Data
public class SchemaOrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ViewType viewType;

    @Column(name = "schemas_order", columnDefinition = "JSON")
    private String order;
}