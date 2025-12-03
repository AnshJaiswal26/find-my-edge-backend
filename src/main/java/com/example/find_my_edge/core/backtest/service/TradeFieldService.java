package com.example.find_my_edge.core.backtest.service;

import com.example.find_my_edge.core.backtest.dto.FieldDataRequest;
import com.example.find_my_edge.core.backtest.dto.FieldDataResponse;
import com.example.find_my_edge.core.backtest.dto.TradeRecordsResponse;
import com.example.find_my_edge.core.backtest.entity.Trade;
import com.example.find_my_edge.core.backtest.entity.TradeField;
import com.example.find_my_edge.core.backtest.repository.TradeFieldRepository;
import com.example.find_my_edge.core.backtest.repository.TradeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;


@Service
@RequiredArgsConstructor
public class TradeFieldService {

    private final TradeRepository tradeRepository;
    private final TradeFieldRepository tradeFieldRepository;

    private final ObjectMapper objectMapper;


    public TradeRecordsResponse addTradeRecords(List<FieldDataRequest> fields) {
        Trade trade = new Trade();

        for (FieldDataRequest dto : fields) {
            TradeField field = toEntity(dto);
            field.setTrade(trade);
            trade.getFields()
                 .add(field);
        }

        Trade saved = tradeRepository.save(trade);
        return toTradeRecordsResponseDto(saved);
    }


    public ResponseEntity<List<TradeRecordsResponse>> getTradeRecords() {
        List<Trade> trades = tradeRepository.findAll();

        List<TradeRecordsResponse> records = trades.stream()
                                                   .map(this::toTradeRecordsResponseDto)
                                                   .toList();

        return new ResponseEntity<>(
                records,
                HttpStatus.OK
        );
    }

    @Transactional
    public ResponseEntity<?> updateTradeRecords(Long tradeId, List<FieldDataRequest> fields) {
        Trade trade = tradeRepository.findById(tradeId)
                                     .orElse(null);
        if (trade == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        trade.getFields()
             .clear();

        for (FieldDataRequest dto : fields) {
            TradeField field = toEntity(dto);
            field.setTrade(trade);
            trade.getFields()
                 .add(field);
        }

        Trade saved = tradeRepository.save(trade);

        return ResponseEntity.ok(toTradeRecordsResponseDto(saved));
    }


    @Transactional
    public ResponseEntity<?> deleteTradeRecord(Long tradeId) {
        tradeRepository.deleteById(tradeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    private TradeRecordsResponse toTradeRecordsResponseDto(Trade trade) {

        return new TradeRecordsResponse(
                trade.getId(),
                trade.getCreatedAt(),
                trade.getFields()
                     .stream()
                     .map(this::toFieldDataResponseDto)
                     .toList()
        );
    }

    private FieldDataResponse toFieldDataResponseDto(TradeField field) {
        List<String> options = null;
        if (field.getOptions() != null) {
            try {
                options = objectMapper.readValue(
                        field.getOptions(),
                        new TypeReference<List<String>>() {
                        }
                );
            } catch (Exception e) {
                throw new RuntimeException(
                        "Failed to parse options to JSON",
                        e
                );
            }
        }
        return new FieldDataResponse(
                field.getId(),
                field.getLabel(),
                field.getType(),
                field.getValue(),
                field.getMappedWith(),
                options
        );
    }


    public TradeField toEntity(FieldDataRequest dto) {
        String jsonOptions = null;

        if (dto.getOptions() != null) {
            try {
                jsonOptions = objectMapper.writeValueAsString(dto.getOptions());

            } catch (Exception e) {
                throw new RuntimeException(
                        "Failed to convert options to JSON",
                        e
                );
            }
        }
        return new TradeField(
                dto.getLabel(),
                dto.getType(),
                dto.getValue(),
                dto.getMappedWith(),
                jsonOptions
        );
    }
}
