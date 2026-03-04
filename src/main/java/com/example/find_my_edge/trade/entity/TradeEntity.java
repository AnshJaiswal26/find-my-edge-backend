package com.example.find_my_edge.trade.entity;

import com.example.find_my_edge.trade.converter.MapToJsonConverter;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "trades",
        uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "externalId"}))
@Data
public class TradeEntity {

    @Id
    private String id;

    private String userId;
    private String externalId;

    // 🔥 structured fields
    private Long date;
    private Integer entryTime;
    private Integer exitTime;

    private String symbol;
    private String direction;

    private Double charges;

    private Double entryPrice;
    private Double exitPrice;

    private Integer qty;

    // 🔥 dynamic fields
    @Column(name = "field_value", columnDefinition = "JSON")
    @Convert(converter = MapToJsonConverter.class)
    private Map<String, Object> values = new HashMap<>();

    private Long createdAt;
    private Long updatedAt;
}