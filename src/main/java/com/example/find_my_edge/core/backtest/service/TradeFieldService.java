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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TradeFieldService {

    private final TradeRepository tradeRepository;
    private final TradeFieldRepository tradeFieldRepository;

    private final ObjectMapper objectMapper;


    public TradeRecordsResponse addTradeRecords(List<FieldDataRequest> fields) {
        Trade trade = tradeRepository.save(new Trade());
        List<TradeField> tradeFields = fields.stream()
                                             .map(field -> toEntity(
                                                     trade.getId(),
                                                     field
                                             ))
                                             .toList();

        List<TradeField> savedTradeFields = tradeFieldRepository.saveAll(tradeFields);
        return new TradeRecordsResponse(
                trade.getId(),
                savedTradeFields.stream()
                                .map(this::toFieldDataResponseDto)
                                .toList()
        );
    }

    public List<TradeRecordsResponse> getTradeRecords() {
        List<TradeField> fields = tradeFieldRepository.findAll();

        return fields.stream()
                     .collect(Collectors.groupingBy(
                             TradeField::getTradeId,
                             LinkedHashMap::new,
                             Collectors.toList()
                     ))
                     .entrySet()
                     .stream()
                     .map(e -> toTradeRecordsResponseDto(
                             e.getKey(),
                             e.getValue()
                     ))
                     .toList();

    }

    @Transactional
    public TradeRecordsResponse updateTradeRecords(Long tradeId, List<FieldDataRequest> fields) {
        tradeFieldRepository.deleteAllByTradeId(tradeId);
        List<TradeField> tradeFields = fields.stream()
                                             .map(field -> toEntity(
                                                     tradeId,
                                                     field
                                             ))
                                             .toList();

        tradeFieldRepository.saveAll(tradeFields);

        return toTradeRecordsResponseDto(
                tradeId,
                tradeFields
        );
    }


    @Transactional
    public ResponseEntity<?> deleteTradeRecord(Long tradeId) {
        tradeFieldRepository.deleteAllByTradeId(tradeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    private TradeRecordsResponse toTradeRecordsResponseDto(Long tradeId, List<TradeField> tradeFields) {
        return new TradeRecordsResponse(
                tradeId,
                tradeFields.stream()
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

    public TradeField toEntity(Long tradeId, FieldDataRequest dto) {
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
                tradeId,
                dto.getLabel(),
                dto.getType(),
                dto.getValue(),
                dto.getMappedWith(),
                jsonOptions
        );
    }


}
