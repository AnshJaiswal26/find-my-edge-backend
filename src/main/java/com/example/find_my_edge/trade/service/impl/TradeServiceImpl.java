package com.example.find_my_edge.trade.service.impl;

import com.example.find_my_edge.common.auth.AuthService;
import com.example.find_my_edge.integrations.borkers.dhan.exception.NoTradesFound;
import com.example.find_my_edge.integrations.borkers.dhan.model.ProcessedTrade;
import com.example.find_my_edge.integrations.borkers.dhan.service.DhanOAuthService;
import com.example.find_my_edge.integrations.borkers.dhan.service.DhanTradeService;
import com.example.find_my_edge.trade.entity.TradeEntity;
import com.example.find_my_edge.trade.exception.TradeNotFoundException;
import com.example.find_my_edge.trade.mapper.ProcessedTradeMapper;
import com.example.find_my_edge.trade.mapper.TradeEntityMapper;
import com.example.find_my_edge.trade.model.Trade;
import com.example.find_my_edge.trade.repository.TradeRepository;
import com.example.find_my_edge.trade.service.TradeService;

import com.example.find_my_edge.workspace.service.WorkspaceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TradeServiceImpl implements TradeService {

    private final AuthService authService;
    private final TradeRepository tradeRepository;
    private final TradeEntityMapper mapper;
    private final ProcessedTradeMapper processedTradeMapper;

    private final WorkspaceService workspaceService;

    private final DhanTradeService dhanTradeService;

    private final DhanOAuthService dhanOAuthService;


    /* ---------------- CREATE ---------------- */

    @Override
    public Trade create(Trade trade) {

        String userId = authService.getCurrentUserId();
        long now = Instant.now().toEpochMilli();

        TradeEntity entity = mapper.toEntity(trade);

        entity.setId(UUID.randomUUID().toString());
        entity.setUserId(userId);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        TradeEntity saved = tradeRepository.save(entity);

        return mapper.toDomain(saved);
    }

    @Override
    public void createAll(List<Trade> trades) {

        String userId = authService.getCurrentUserId();
        long now = Instant.now().toEpochMilli();

        List<TradeEntity> entityList = new ArrayList<>();

        for (Trade trade : trades) {
            TradeEntity entity = mapper.toEntity(trade);
            entity.setId(UUID.randomUUID().toString());
            entity.setUserId(userId);
            entity.setCreatedAt(now);
            entity.setUpdatedAt(now);

            entityList.add(entity);
        }

        tradeRepository.saveAll(entityList);
    }

    /* ---------------- UPDATE ---------------- */

    @Override
    public Trade update(String id, Trade trade) {

        String userId = authService.getCurrentUserId();

        TradeEntity existing = tradeRepository
                .findByIdAndUserId(id, userId)
                .orElseThrow(() -> new TradeNotFoundException(id));

        TradeEntity updated = mapper.toEntity(trade);

        updated.setId(existing.getId());
        updated.setUserId(userId);
        updated.setCreatedAt(existing.getCreatedAt()); // preserve
        updated.setUpdatedAt(Instant.now().toEpochMilli());

        TradeEntity saved = tradeRepository.save(updated);

        return mapper.toDomain(saved);
    }

    /* ---------------- GET BY ID ---------------- */

    @Transactional
    @Override
    public Trade getById(String id) {

        String userId = authService.getCurrentUserId();

        TradeEntity entity = tradeRepository
                .findByIdAndUserId(id, userId)
                .orElseThrow(() -> new TradeNotFoundException(id));

        return mapper.toDomain(entity);
    }

    /* ---------------- GET ALL ---------------- */

    @Transactional
    @Override
    public List<Trade> getAll() {

        String userId = authService.getCurrentUserId();

        return tradeRepository.findAllByUserIdOrderByDateAscEntryTimeAsc(userId)
                              .stream()
                              .map(mapper::toDomain)
                              .toList();
    }

    /* ---------------- DELETE ---------------- */

    @Override
    public void delete(String id) {

        String userId = authService.getCurrentUserId();

        TradeEntity entity = tradeRepository
                .findByIdAndUserId(id, userId)
                .orElseThrow(() -> new TradeNotFoundException(id));

        tradeRepository.delete(entity);
        workspaceService.removeTradeReferences(id);
    }


    @Override
    public void deleteAll() {

        String userId = authService.getCurrentUserId();

        tradeRepository.deleteByUserId(userId);
    }

    public void upsertTrades(List<Trade> trades) {

        String userId = authService.getCurrentUserId();
        long now = Instant.now().toEpochMilli();

        List<TradeEntity> entities = new ArrayList<>();

        for (Trade trade : trades) {

            String externalId = trade.getExternalId(); // 🔥 YOUR LINE

            TradeEntity existing = tradeRepository
                    .findByUserIdAndExternalId(userId, externalId)
                    .orElse(null);

            TradeEntity entity = mapper.toEntity(trade);

            if (existing != null) {
                // ✅ UPDATE existing trade
                entity.setId(existing.getId());
                entity.setCreatedAt(existing.getCreatedAt());
            } else {
                // ✅ INSERT new trade
                entity.setId(UUID.randomUUID().toString());
                entity.setCreatedAt(now);
            }

            entity.setUserId(userId);
            entity.setExternalId(externalId); // 🔥 important
            entity.setUpdatedAt(now);

            entities.add(entity);
        }

        tradeRepository.saveAll(entities);
    }

    private List<ProcessedTrade> fetchAllPages(LocalDate fromDate, LocalDate toDate) {

        List<ProcessedTrade> allTrades = new ArrayList<>();
        int page = 0;

        while (true) {
            try {
                List<ProcessedTrade> trades =
                        dhanTradeService.getTrades(fromDate, toDate, page);

                if (trades == null || trades.isEmpty()) break;

                allTrades.addAll(trades);
                page++;

            } catch (NoTradesFound e) {
                break; // no more pages
            }
        }

        return allTrades;
    }

    @Override
    public List<Trade> fetchAllAndSave() {

        String userId = authService.getCurrentUserId();

        LocalDate fromDate = LocalDate.of(2000, 1, 1);
        LocalDate toDate = LocalDate.now();

        List<ProcessedTrade> raw = fetchAllPages(fromDate, toDate);

        List<Trade> trades = mapAndSort(raw);

        upsertTrades(trades);

        updateLastFetched(userId);

        return trades;
    }

    @Override
    public List<Trade> fetchIncrementalAndSave() {

        String userId = authService.getCurrentUserId();

        Instant lastFetchedAt = dhanOAuthService.getLastFetchedAt();

        ZoneId zone = ZoneId.systemDefault();

        LocalDateTime fromDateTime = (lastFetchedAt != null)
                                     ? LocalDateTime.ofInstant(lastFetchedAt, zone).minusDays(1)
                                     : LocalDateTime.of(2000, 1, 1, 0, 0);

        LocalDate fromDate = fromDateTime.toLocalDate();
        LocalDate toDate = LocalDate.now();

        List<ProcessedTrade> raw = fetchAllPages(fromDate, toDate);

        List<Trade> trades = mapAndSort(raw);

        upsertTrades(trades);

        updateLastFetched(userId);

        return trades;
    }

    @Override
    public List<Trade> fetchCustomAndSave(LocalDate fromDate, LocalDate toDate) {

        List<ProcessedTrade> raw = fetchAllPages(fromDate, toDate);

        List<Trade> trades = mapAndSort(raw);

        upsertTrades(trades); // 🔥 overwrite handled automatically

        return trades;
    }

    private void updateLastFetched(String userId) {
        dhanOAuthService.updateLastFetchedAt(
                LocalDate.now()
                         .plusDays(1)
                         .atStartOfDay(ZoneId.of("Asia/Kolkata"))
                         .toInstant(),
                userId
        );
    }


    private List<Trade> mapAndSort(List<ProcessedTrade> trades) {

        return trades.stream()
                     .map(processedTradeMapper::mapToTrade)
                     .sorted(
                             Comparator
                                     .comparing((Trade t) -> getLong(t, "date"))
                                     .thenComparing(t -> getLong(t, "entryTime"))
                     )
                     .toList();
    }

    private long getLong(Trade t, String key) {
        Object val = t.getValues().get(key);
        return val == null ? 0L : ((Number) val).longValue();
    }
}