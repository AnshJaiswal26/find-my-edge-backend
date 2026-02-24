package com.example.find_my_edge.api.trade_import.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
public class ImportedTradeResponseDTO {
    Long tradeId;
    Timestamp createdAt;
    List<FieldDataResponseDTO> fields;
}
