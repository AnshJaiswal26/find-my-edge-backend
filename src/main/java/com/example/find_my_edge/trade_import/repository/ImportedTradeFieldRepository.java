package com.example.find_my_edge.trade_import.repository;

import com.example.find_my_edge.trade_import.entity.ImportedTradeFieldEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportedTradeFieldRepository extends JpaRepository<ImportedTradeFieldEntity, Long> {

    void deleteAllByImportId(Long id);
}
