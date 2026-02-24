package com.example.find_my_edge.trade_import.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportedTradeFieldEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "trade_id")
    private ImportedTradeEntity importedTradeEntity;

    private String label;
    private String type;
    private String value;
    private String mappedWith;
    private int mappedColumn;


    @Column(columnDefinition = "JSON")
    private String options;

}
