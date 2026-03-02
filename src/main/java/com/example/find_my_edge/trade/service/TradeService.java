package com.example.find_my_edge.trade.service;

import com.example.find_my_edge.trade.model.Trade;
import jakarta.transaction.Transactional;

import java.util.List;

public interface TradeService {

    Trade create(Trade trade);

    Trade update(String id, Trade trade);

    Trade getById(String id);

    List<Trade> getAll();

    void delete(String id);

    void createAll(List<Trade> trades);

    void deleteAll();

    List<Trade> fetchAllAndSave();

    List<Trade> fetchIncrementalAndSave();

    List<Trade> fetchCustomAndSave(String fromDate, String toDate);
}