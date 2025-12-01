package com.example.find_my_edge.core.backtest.repository;

import com.example.find_my_edge.core.backtest.entity.TradeField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeFieldRepository extends JpaRepository<TradeField, Long> {


    List<TradeField> findAllByTradeId(Long id);

    void deleteAllByTradeId(Long id);
}
