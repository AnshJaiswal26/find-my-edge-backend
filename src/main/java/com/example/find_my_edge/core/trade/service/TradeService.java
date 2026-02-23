package com.example.find_my_edge.core.trade.service;

import com.example.find_my_edge.core.trade.dto.TradeRequestDTO;
import com.example.find_my_edge.core.trade.dto.TradeResponseDTO;
import jakarta.transaction.Transactional;

import java.util.List;

public interface TradeService {
    TradeResponseDTO create(TradeRequestDTO request);

    TradeResponseDTO update(String id, TradeRequestDTO request);

    @Transactional(Transactional.TxType.SUPPORTS)
    TradeResponseDTO getById(String id);

    @Transactional(Transactional.TxType.SUPPORTS)
    List<TradeResponseDTO> getAll();

    void delete(String id);
}
