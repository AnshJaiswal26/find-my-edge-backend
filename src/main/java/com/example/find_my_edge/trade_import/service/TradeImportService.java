package com.example.find_my_edge.trade_import.service;

import com.example.find_my_edge.trade_import.dto.FieldDataRequestDto;
import com.example.find_my_edge.trade_import.dto.ImportedTradeResponseDto;
import com.example.find_my_edge.trade_import.entity.ImportedTradeEntity;
import com.example.find_my_edge.trade_import.entity.ImportedTradeFieldEntity;

import java.util.List;

public interface TradeImportService {
    ImportedTradeResponseDto create(List<FieldDataRequestDto> fields);

    List<ImportedTradeResponseDto> getAll();

    ImportedTradeResponseDto update(Long importId, List<FieldDataRequestDto> fields);

    void delete(Long importId);
}
