package com.example.find_my_edge.core.trade_import.service.impl;

import com.example.find_my_edge.core.trade_import.dto.FieldDataRequestDTO;
import com.example.find_my_edge.core.trade_import.dto.ImportedTradeResponseDTO;
import com.example.find_my_edge.core.trade_import.entity.ImportedTradeEntity;
import com.example.find_my_edge.core.trade_import.entity.ImportedTradeFieldEntity;
import com.example.find_my_edge.core.trade_import.mapper.ImportedTradeFieldMapper;
import com.example.find_my_edge.core.trade_import.mapper.ImportedTradeMapper;
import com.example.find_my_edge.core.trade_import.repository.ImportedTradeRepository;
import com.example.find_my_edge.core.trade_import.exception.ImportedTradeNotFoundException;
import com.example.find_my_edge.core.trade_import.service.TradeImportService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class TradeImportServiceImpl implements TradeImportService {

    private final ImportedTradeRepository importedTradeRepository;
    private final ImportedTradeMapper importedTradeMapper;
    private final ImportedTradeFieldMapper fieldMapper;

    @Override
    public ImportedTradeResponseDTO create(List<FieldDataRequestDTO> fields) {
        ImportedTradeEntity importedTradeEntity = new ImportedTradeEntity();

        for (FieldDataRequestDTO dto : fields) {
            ImportedTradeFieldEntity field = fieldMapper.toEntity(dto);
            field.setImportedTradeEntity(importedTradeEntity);
            importedTradeEntity.getFields().add(field);
        }

        ImportedTradeEntity saved = importedTradeRepository.save(importedTradeEntity);

        return importedTradeMapper.toResponseDTO(saved);

    }

    @Override
    public List<ImportedTradeResponseDTO> getAll() {
        List<ImportedTradeEntity> importedTradeEntities = importedTradeRepository.findAll();

        return importedTradeEntities.stream()
                                    .map(importedTradeMapper::toResponseDTO)
                                    .toList();
    }

    @Transactional
    @Override
    public ImportedTradeResponseDTO update(Long importId, List<FieldDataRequestDTO> fields) {
        ImportedTradeEntity importedTradeEntity = importedTradeRepository.findById(importId)
                                                                         .orElseThrow(() -> new ImportedTradeNotFoundException("Imported Trade not found with id " + importId));

        importedTradeEntity.getFields().clear();

        for (FieldDataRequestDTO dto : fields) {
            ImportedTradeFieldEntity field = fieldMapper.toEntity(dto);
            field.setImportedTradeEntity(importedTradeEntity);
            importedTradeEntity.getFields()
                               .add(field);
        }

        ImportedTradeEntity saved = importedTradeRepository.save(importedTradeEntity);

        return importedTradeMapper.toResponseDTO(saved);
    }

    @Transactional
    @Override
    public void delete(Long importId) {

        if (!importedTradeRepository.existsById(importId)) {
            throw new ImportedTradeNotFoundException("Imported Trade not found with id " + importId);
        }

        importedTradeRepository.deleteById(importId);
    }
}
