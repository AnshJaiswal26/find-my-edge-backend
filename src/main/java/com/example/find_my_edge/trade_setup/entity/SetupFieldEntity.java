package com.example.find_my_edge.trade_setup.entity;

import com.example.find_my_edge.trade_setup.enums.Tag;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "setup_fields")
@Data
public class SetupFieldEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "trade_setup_id")
    private TradeSetupEntity tradeSetup;

    private String mappedSchemaId;

    @Column(name = "condition_type")
    private String condition; // <= , >=, ==..., for categorical: "text contains", "text starts with"...

    @Column(name = "from_value")
    private Double from;

    @Column(name = "to_value")
    private Double to;

    private String expected; // 100, 200, 300...,  or for categorical: "A", "B", "C"...

    @Enumerated(EnumType.STRING)
    private Tag tag;
}
