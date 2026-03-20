package com.example.find_my_edge.trade_setup.entity;

import com.example.find_my_edge.common.converter.MapToJsonConverter;
import com.example.find_my_edge.trade.entity.TradeEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.*;

@Entity
@Data
public class TradeSetupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private UUID userId;

    private String name;

    private String imageUrl;
    private String imagePublicId;

    @OneToMany(mappedBy = "tradeSetup")
    private List<TradeEntity> trades;

    @Column(columnDefinition = "JSON")
    private String fieldOrder;

    @OneToMany(mappedBy = "tradeSetup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SetupFieldEntity> fields = new ArrayList<>();
}