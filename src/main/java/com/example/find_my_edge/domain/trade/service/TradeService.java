package com.example.find_my_edge.domain.trade.service;

import com.example.find_my_edge.domain.trade.model.Trade;
import jakarta.transaction.Transactional;

import java.util.List;

public interface TradeService {

    Trade create(Trade trade);

    Trade update(String id, Trade trade);

    @Transactional(Transactional.TxType.SUPPORTS)
    Trade getById(String id);

    @Transactional(Transactional.TxType.SUPPORTS)
    List<Trade> getAll();

    void delete(String id);

    void createAll(List<Trade> trades);

    void deleteAll();
}