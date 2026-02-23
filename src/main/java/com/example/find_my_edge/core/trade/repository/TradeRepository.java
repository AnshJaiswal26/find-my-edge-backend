package com.example.find_my_edge.core.trade.repository;

import com.example.find_my_edge.core.trade.entity.TradeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TradeRepository extends JpaRepository<TradeEntity, String> {

    Optional<TradeEntity> findByIdAndUserId(String id, String userId);

    List<TradeEntity> findAllByUserId(String userId);
}
