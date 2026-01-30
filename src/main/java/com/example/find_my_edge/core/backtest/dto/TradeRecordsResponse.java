package com.example.find_my_edge.core.backtest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
public class TradeRecordsResponse {
    Long tradeId;
    Timestamp createdAt;
    List<FieldDataResponse> fields;
}
