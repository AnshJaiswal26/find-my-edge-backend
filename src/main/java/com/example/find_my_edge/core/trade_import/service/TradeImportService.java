package com.example.find_my_edge.core.trade_import.service;

import com.example.find_my_edge.core.trade_import.dto.FieldDataRequestDTO;
import com.example.find_my_edge.core.trade_import.dto.ImportedTradeResponseDTO;

import java.util.List;

public interface TradeImportService {
    ImportedTradeResponseDTO create(List<FieldDataRequestDTO> fields);

    List<ImportedTradeResponseDTO> getAll();

    ImportedTradeResponseDTO update(Long importId, List<FieldDataRequestDTO> fields);

    void delete(Long importId);
}
