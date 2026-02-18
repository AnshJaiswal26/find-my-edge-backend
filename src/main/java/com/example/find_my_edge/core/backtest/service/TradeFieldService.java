package com.example.find_my_edge.core.backtest.service;

import com.example.find_my_edge.core.backtest.dto.FieldDataRequest;
import com.example.find_my_edge.core.backtest.dto.TradeRecordsResponse;
import com.example.find_my_edge.core.backtest.entity.Trade;
import com.example.find_my_edge.core.backtest.entity.TradeField;
import com.example.find_my_edge.core.backtest.mapper.TradeFieldMapper;
import com.example.find_my_edge.core.backtest.mapper.TradeMapper;
import com.example.find_my_edge.core.backtest.repository.TradeRepository;
import com.example.find_my_edge.common.exceptions.TradeNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class TradeFieldService {

    private final TradeRepository tradeRepository;
    private final TradeMapper tradeMapper;
    private final TradeFieldMapper fieldMapper;

    public TradeRecordsResponse addTradeRecords(List<FieldDataRequest> fields) {
        Trade trade = new Trade();

        for (FieldDataRequest dto : fields) {
            TradeField field = fieldMapper.toEntity(dto);
            field.setTrade(trade);
            trade.getFields().add(field);
        }

        Trade saved = tradeRepository.save(trade);

        return tradeMapper.toResponse(saved);

    }


    public List<TradeRecordsResponse> getTradeRecords() {
        List<Trade> trades = tradeRepository.findAll();

        return trades.stream()
                     .map(tradeMapper::toResponse)
                     .toList();


    }

    @Transactional
    public TradeRecordsResponse updateTradeRecords(Long tradeId, List<FieldDataRequest> fields) {
        Optional<Trade> tradeOptional = tradeRepository.findById(tradeId);

        Trade trade = tradeOptional.orElseThrow(() -> new TradeNotFoundException("Trade not found with id " + tradeId));

        trade.getFields()
             .clear();

        for (FieldDataRequest dto : fields) {
            TradeField field = fieldMapper.toEntity(dto);
            field.setTrade(trade);
            trade.getFields()
                 .add(field);
        }

        Trade saved = tradeRepository.save(trade);

        return tradeMapper.toResponse(saved);
    }

    @Transactional
    public void deleteTradeRecord(Long tradeId) {

        if (!tradeRepository.existsById(tradeId)) {
            throw new TradeNotFoundException("Trade not found with id " + tradeId);
        }

        tradeRepository.deleteById(tradeId);
    }
}
