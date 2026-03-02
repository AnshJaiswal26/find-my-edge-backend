package com.example.find_my_edge.trade_import.service.impl;

import com.example.find_my_edge.trade_import.dto.FieldDataRequestDto;
import com.example.find_my_edge.trade_import.dto.ImportedTradeResponseDto;
import com.example.find_my_edge.trade_import.entity.ImportedTradeEntity;
import com.example.find_my_edge.trade_import.entity.ImportedTradeFieldEntity;
import com.example.find_my_edge.trade_import.mapper.ImportedTradeFieldMapper;
import com.example.find_my_edge.trade_import.mapper.ImportedTradeMapper;
import com.example.find_my_edge.trade_import.repository.ImportedTradeRepository;
import com.example.find_my_edge.trade_import.exception.ImportedTradeNotFoundException;
import com.example.find_my_edge.trade_import.service.TradeImportService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class TradeImportServiceImpl implements TradeImportService {

    private final ImportedTradeRepository importedTradeRepository;

    private final ImportedTradeFieldMapper fieldMapper;
    private final ImportedTradeMapper importedTradeMapper;

    @Override
    public ImportedTradeResponseDto create(List<FieldDataRequestDto> request) {
        List<ImportedTradeFieldEntity> fields = request.stream()
                                                       .map(fieldMapper::toEntity).toList();

        ImportedTradeEntity importedTradeEntity = new ImportedTradeEntity();

        for (ImportedTradeFieldEntity field : fields) {
            field.setTrade(importedTradeEntity);
            importedTradeEntity.getFields().add(field);
        }

        return importedTradeMapper.toResponse(
                importedTradeRepository.save(importedTradeEntity)
        );

    }

    @Override
    public List<ImportedTradeResponseDto> getAll() {
        List<ImportedTradeEntity> all = importedTradeRepository.findAll();
        return all.stream()
                  .map(importedTradeMapper::toResponse)
                  .toList();
    }

    @Transactional
    @Override
    public ImportedTradeResponseDto update(Long importId, List<FieldDataRequestDto> dto) {

        List<ImportedTradeFieldEntity> fields = dto.stream()
                                                   .map(fieldMapper::toEntity).toList();

        ImportedTradeEntity importedTradeEntity =
                importedTradeRepository
                        .findById(importId)
                        .orElseThrow(() -> new ImportedTradeNotFoundException(importId));

        importedTradeEntity.getFields().clear();
        importedTradeEntity.getFields().addAll(fields);

        return importedTradeMapper.toResponse(importedTradeRepository.save(importedTradeEntity));
    }

    @Transactional
    @Override
    public void delete(Long importId) {

        if (!importedTradeRepository.existsById(importId)) {
            throw new ImportedTradeNotFoundException(importId);
        }

        importedTradeRepository.deleteById(importId);
    }
}
