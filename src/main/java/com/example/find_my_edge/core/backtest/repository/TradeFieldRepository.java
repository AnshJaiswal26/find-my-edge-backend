package com.example.find_my_edge.core.backtest.repository;

import com.example.find_my_edge.core.backtest.entity.TradeField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeFieldRepository extends JpaRepository<TradeField, Long> {

    void deleteAllByTradeId(Long id);
}
