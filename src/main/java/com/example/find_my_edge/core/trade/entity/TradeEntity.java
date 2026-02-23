package com.example.find_my_edge.core.trade.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "trades")
@Data
public class TradeEntity {

    @Id
    private String id;

    private String userId;

    @ElementCollection
    @CollectionTable(
            name = "trade_values",
            joinColumns = @JoinColumn(name = "trade_id"),
            indexes = {
                    @Index(name = "idx_trade_values_trade_id", columnList = "trade_id"),
                    @Index(name = "idx_trade_values_field_key", columnList = "field_key"),
                    @Index(name = "idx_trade_values_key_value", columnList = "field_key, field_value")
            }
    )
    @MapKeyColumn(name = "field_key") // = SchemaEntity.id
    @Column(name = "field_value", columnDefinition = "TEXT")
    private Map<String, String> values = new HashMap<>();

    private Long createdAt;
    private Long updatedAt;
}