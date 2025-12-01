package com.example.find_my_edge.core.backtest.repository;

import com.example.find_my_edge.core.backtest.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {

}
