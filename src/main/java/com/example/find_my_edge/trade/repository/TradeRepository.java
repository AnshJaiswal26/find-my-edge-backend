package com.example.find_my_edge.trade.repository;

import com.example.find_my_edge.trade.entity.TradeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TradeRepository extends JpaRepository<TradeEntity, String> {

    Optional<TradeEntity> findByIdAndUserId(String id, UUID userId);

    List<TradeEntity> findAllByUserId(UUID userId);

    List<TradeEntity> findAllByUserIdOrderByDateAscEntryTimeAsc(UUID userId);

    void deleteByUserId(UUID userId);

    Optional<TradeEntity> findByUserIdAndExternalId(UUID userId, String externalId);
}
