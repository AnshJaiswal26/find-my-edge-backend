package com.example.find_my_edge.core.backtest.service;

import com.example.find_my_edge.common.response.ApiResponse;
import com.example.find_my_edge.common.utils.JsonUtil;
import com.example.find_my_edge.core.backtest.dto.FieldDataRequest;
import com.example.find_my_edge.core.backtest.dto.FieldDataResponse;
import com.example.find_my_edge.core.backtest.dto.TradeRecordsResponse;
import com.example.find_my_edge.core.backtest.entity.Trade;
import com.example.find_my_edge.core.backtest.entity.TradeField;
import com.example.find_my_edge.core.backtest.repository.TradeRepository;
import com.example.find_my_edge.common.exceptions.TradeNotFound;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class TradeFieldService {

    private final TradeRepository tradeRepository;

    public ResponseEntity<ApiResponse<?>> addTradeRecords(List<FieldDataRequest> fields) {
        Trade trade = new Trade();

        for (FieldDataRequest dto : fields) {
            TradeField field = toEntity(dto);
            field.setTrade(trade);
            trade.getFields()
                 .add(field);
        }

        Trade saved = tradeRepository.save(trade);

        return ResponseEntity.ok(
                ApiResponse.builder()
                           .success(true)
                           .status(HttpStatus.OK.value())
                           .message("Trade Record added successfully")
                           .data(toTradeRecordsResponseDto(saved))
                           .meta(Map.of("empty", false, "count", 1))
                           .build()
        );
    }


    public ResponseEntity<ApiResponse<?>> getTradeRecords() {
        List<Trade> trades = tradeRepository.findAll();

        List<TradeRecordsResponse> records = trades.stream()
                                                   .map(this::toTradeRecordsResponseDto)
                                                   .toList();

        return ResponseEntity.ok(
                ApiResponse.builder()
                           .success(true)
                           .status(HttpStatus.OK.value())
                           .message("Trade Records found successfully")
                           .data(records)
                           .meta(Map.of("empty", false, "count", records.size()))
                           .build()
        );
    }

    @Transactional
    public ResponseEntity<ApiResponse<?>> updateTradeRecords(Long tradeId, List<FieldDataRequest> fields) {
        Optional<Trade> tradeOptional = tradeRepository.findById(tradeId);

        Trade trade = tradeOptional.orElseThrow(() -> new TradeNotFound("Trade not found with id " + tradeId));

        trade.getFields()
             .clear();

        for (FieldDataRequest dto : fields) {
            TradeField field = toEntity(dto);
            field.setTrade(trade);
            trade.getFields()
                 .add(field);
        }

        Trade saved = tradeRepository.save(trade);

        return ResponseEntity.ok(
                ApiResponse.builder()
                           .success(true)
                           .status(HttpStatus.OK.value())
                           .message("Trade Record updated successfully")
                           .data(toTradeRecordsResponseDto(saved))
                           .meta(Map.of("empty", false, "count", 1))
                           .build()
        );
    }


    @Transactional
    public ResponseEntity<ApiResponse<?>> deleteTradeRecord(Long tradeId) {
        tradeRepository.deleteById(tradeId);
        return ResponseEntity.ok(
                ApiResponse.builder()
                           .success(true)
                           .status(HttpStatus.OK.value())
                           .message("Trade Record deleted successfully")
                           .data(null)
                           .meta(Map.of("empty", true, "count", 0))
                           .build()
        );
    }


    private TradeRecordsResponse toTradeRecordsResponseDto(Trade trade) {

        return new TradeRecordsResponse(
                trade.getId(), trade.getCreatedAt(), trade.getFields()
                                                          .stream()
                                                          .map(this::toFieldDataResponseDto)
                                                          .toList()
        );
    }

    private FieldDataResponse toFieldDataResponseDto(TradeField field) {
        List<String> options = field.getOptions() != null
                               ? JsonUtil.toList(field.getOptions())
                               : null;

        return new FieldDataResponse(
                field.getId(),
                field.getLabel(),
                field.getType(),
                field.getValue(),
                field.getMappedWith(), options
        );
    }


    public TradeField toEntity(FieldDataRequest dto) {
        String jsonOptions = dto.getOptions() != null
                             ? JsonUtil.toJSON(dto.getOptions())
                             : null;

        return new TradeField(
                dto.getLabel(),
                dto.getType(),
                dto.getValue(),
                dto.getMappedWith(),
                jsonOptions
        );
    }
}
