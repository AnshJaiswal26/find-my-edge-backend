package com.example.find_my_edge.trade_import.service.impl;

import com.example.find_my_edge.trade_import.entity.ImportedTradeEntity;
import com.example.find_my_edge.trade_import.entity.ImportedTradeFieldEntity;
import com.example.find_my_edge.api.trade_import.mapper.ImportedTradeFieldMapper;
import com.example.find_my_edge.api.trade_import.mapper.ImportedTradeMapper;
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


    @Override
    public ImportedTradeEntity create(List<ImportedTradeFieldEntity> fields) {
        ImportedTradeEntity importedTradeEntity = new ImportedTradeEntity();

        for (ImportedTradeFieldEntity field : fields) {
            field.setTrade(importedTradeEntity);
            importedTradeEntity.getFields().add(field);
        }

        return importedTradeRepository.save(importedTradeEntity);

    }

    @Override
    public List<ImportedTradeEntity> getAll() {
        return importedTradeRepository.findAll();
    }

    @Transactional
    @Override
    public ImportedTradeEntity update(Long importId, List<ImportedTradeFieldEntity> fields) {
        ImportedTradeEntity importedTradeEntity = importedTradeRepository.findById(importId)
                                                                         .orElseThrow(() -> new ImportedTradeNotFoundException("Imported Trade not found with id " + importId));

        importedTradeEntity.getFields().clear();
        importedTradeEntity.getFields().addAll(fields);

        return importedTradeRepository.save(importedTradeEntity);
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
