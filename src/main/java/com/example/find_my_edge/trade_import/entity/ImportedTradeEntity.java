package com.example.find_my_edge.trade_import.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class ImportedTradeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private Timestamp createdAt;

    @OneToMany(mappedBy = "trade", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImportedTradeFieldEntity> fields = new ArrayList<>();

}
