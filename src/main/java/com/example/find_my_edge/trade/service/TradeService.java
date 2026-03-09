package com.example.find_my_edge.trade.service;

import com.example.find_my_edge.analytics.model.RecomputeResult;
import com.example.find_my_edge.trade.model.Trade;
import com.example.find_my_edge.trade.model.TradeBundle;

import java.time.LocalDate;
import java.util.List;

public interface TradeService {

    Trade create(Trade trade);

    Trade update(String id, Trade trade);

    RecomputeResult updateValue(String tradeId, String field, Object value);

    Trade getById(String id);

    TradeBundle getTradeBundle();

    List<Trade> getAll();

    void delete(String id);

    void createAll(List<Trade> trades);

    void deleteAll();

    void upsertTrades(List<Trade> trades);
}