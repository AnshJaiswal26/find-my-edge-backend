package com.example.find_my_edge.trade_import.service;

import com.example.find_my_edge.trade_import.entity.ImportedTradeEntity;
import com.example.find_my_edge.trade_import.entity.ImportedTradeFieldEntity;

import java.util.List;

public interface TradeImportService {
    ImportedTradeEntity create(List<ImportedTradeFieldEntity> fields);

    List<ImportedTradeEntity> getAll();

    ImportedTradeEntity update(Long importId, List<ImportedTradeFieldEntity> fields);

    void delete(Long importId);
}
