package com.example.find_my_edge.core.backtest.service;

import com.example.find_my_edge.common.enums.ResponseState;
import com.example.find_my_edge.common.response.ApiResponse;
import com.example.find_my_edge.core.backtest.dto.FieldDataRequest;
import com.example.find_my_edge.core.backtest.dto.TradeRecordsResponse;
import com.example.find_my_edge.core.backtest.entity.Trade;
import com.example.find_my_edge.core.backtest.entity.TradeField;
import com.example.find_my_edge.core.backtest.mapper.TradeFieldMapper;
import com.example.find_my_edge.core.backtest.mapper.TradeMapper;
import com.example.find_my_edge.core.backtest.repository.TradeRepository;
import com.example.find_my_edge.common.exceptions.TradeNotFound;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class TradeFieldService {

    private final TradeRepository tradeRepository;
    private final TradeMapper tradeMapper;
    private final TradeFieldMapper fieldMapper;

    public ResponseEntity<ApiResponse<Object>> addTradeRecords(List<FieldDataRequest> fields) {
        Trade trade = new Trade();

        for (FieldDataRequest dto : fields) {
            TradeField field = fieldMapper.toEntity(dto);
            field.setTrade(trade);
            trade.getFields().add(field);
        }

        Trade saved = tradeRepository.save(trade);

        return ResponseEntity.ok(ApiResponse.builder()
                                            .state(ResponseState.SUCCESS)
                                            .httpStatus(HttpStatus.OK.value())
                                            .message("Trade Record added successfully")
                                            .data(tradeMapper.toResponse(saved))
                                            .meta(Map.of("empty", false, "count", 1))
                                            .build());
    }


    public ResponseEntity<ApiResponse<Object>> getTradeRecords() {
        List<Trade> trades = tradeRepository.findAll();

        List<TradeRecordsResponse> records = trades.stream()
                                                   .map(tradeMapper::toResponse)
                                                   .toList();

        return ResponseEntity.ok(ApiResponse.builder()
                                            .state(ResponseState.SUCCESS)
                                            .httpStatus(HttpStatus.OK.value())
                                            .message("Trade Records found successfully")
                                            .data(records)
                                            .meta(Map.of("empty", false, "count", records.size()))
                                            .build());
    }

    @Transactional
    public ResponseEntity<ApiResponse<Object>> updateTradeRecords(Long tradeId, List<FieldDataRequest> fields) {
        Optional<Trade> tradeOptional = tradeRepository.findById(tradeId);

        Trade trade = tradeOptional.orElseThrow(() -> new TradeNotFound("Trade not found with id " + tradeId));

        trade.getFields()
             .clear();

        for (FieldDataRequest dto : fields) {
            TradeField field = fieldMapper.toEntity(dto);
            field.setTrade(trade);
            trade.getFields()
                 .add(field);
        }

        Trade saved = tradeRepository.save(trade);

        return ResponseEntity.ok(ApiResponse.builder()
                                            .state(ResponseState.SUCCESS)
                                            .httpStatus(HttpStatus.OK.value())
                                            .message("Trade Record updated successfully")
                                            .data(tradeMapper.toResponse(saved))
                                            .meta(Map.of("empty", false, "count", 1))
                                            .build());
    }

    @Transactional
    public ResponseEntity<ApiResponse<Object>> deleteTradeRecord(Long tradeId) {
        tradeRepository.deleteById(tradeId);
        return ResponseEntity.ok(ApiResponse.builder()
                                            .state(ResponseState.SUCCESS)
                                            .httpStatus(HttpStatus.OK.value())
                                            .message("Trade Record deleted successfully")
                                            .data(null)
                                            .meta(Map.of("empty", true, "count", 0))
                                            .build());
    }

}
