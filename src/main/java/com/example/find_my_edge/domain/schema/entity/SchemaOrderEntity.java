package com.example.find_my_edge.domain.schema.entity;

import com.example.find_my_edge.domain.schema.enums.ViewType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class SchemaOrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Enumerated(EnumType.STRING)
    private ViewType viewType;

    @Column(name = "schemas_order", columnDefinition = "TEXT")
    private String order;
}
