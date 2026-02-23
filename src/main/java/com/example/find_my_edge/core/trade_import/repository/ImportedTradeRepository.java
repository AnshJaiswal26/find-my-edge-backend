package com.example.find_my_edge.core.trade_import.repository;

import com.example.find_my_edge.core.trade_import.entity.ImportedTradeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportedTradeRepository extends JpaRepository<ImportedTradeEntity, Long> {

}
