package com.example.find_my_edge.domain.trade.repository;

import com.example.find_my_edge.domain.trade.entity.TradeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TradeRepository extends JpaRepository<TradeEntity, String> {

    Optional<TradeEntity> findByIdAndUserId(String id, String userId);

    List<TradeEntity> findAllByUserId(String userId);
}
