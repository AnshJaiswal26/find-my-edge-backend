package com.example.find_my_edge.domain.schema.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class SchemaOrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    String userId;

    @Column(name = "schemas_order", columnDefinition = "TEXT")
    String order;
}
