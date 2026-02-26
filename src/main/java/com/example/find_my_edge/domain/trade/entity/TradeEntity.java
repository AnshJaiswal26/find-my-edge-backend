package com.example.find_my_edge.domain.trade.entity;

import com.example.find_my_edge.domain.trade.converter.MapToJsonConverter;
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

    @Column(name = "field_value", columnDefinition = "JSON")
    @Convert(converter = MapToJsonConverter.class)
    private Map<String, Object> values = new HashMap<>();

    private Long createdAt;
    private Long updatedAt;
}