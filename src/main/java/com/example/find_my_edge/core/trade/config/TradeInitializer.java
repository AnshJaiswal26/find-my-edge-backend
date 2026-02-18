package com.example.find_my_edge.core.trade.config;

import com.example.find_my_edge.core.trade.dto.Trade;
import com.example.find_my_edge.core.trade.service.TradeService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class TradeInitializer {

    private final TradeService tradeService;
    private final Random random = new Random();

    private static final int DAYS = 60;
    private static final double RISK_PER_TRADE = 500;

    private static final LocalTime MARKET_OPEN = LocalTime.of(9, 15);
    private static final LocalTime MARKET_CLOSE = LocalTime.of(15, 30);

    @PostConstruct
    public void init() {
        List<Trade> trades = generateTrades();
        tradeService.saveAll(trades); // 🔥 seed into memory DB
    }

    private List<Trade> generateTrades() {
        List<Trade> trades = new ArrayList<>();

        LocalDate startDate = LocalDate.now().minusDays(DAYS);
        int tradeCounter = 1;

        for (int d = 0; d < DAYS; d++) {
            LocalDate tradeDate = startDate.plusDays(d);

            int tradesToday = 1 + random.nextInt(5);
            LocalTime lastExitTime = MARKET_OPEN;

            for (int t = 0; t < tradesToday; t++) {

                Trade trade = new Trade();

                /* -------- TIME -------- */
                LocalTime entryTime = randomTimeAfter(lastExitTime);
                long durationSeconds = randomDuration();
                LocalTime exitTime = entryTime.plusSeconds(durationSeconds);

                if (exitTime.isAfter(MARKET_CLOSE)) {
                    exitTime = MARKET_CLOSE.minusMinutes(15);
                    entryTime = entryTime.minusMinutes(30);
                }

                lastExitTime = exitTime;

                /* -------- PRICE -------- */
                double entry = randomPrice(100, 400);
                int qty = randomQty();

                boolean isWin = random.nextDouble() < 0.48;

                double move = isWin
                              ? randomRange(0.3, 1.5) * (RISK_PER_TRADE / qty)
                              : -randomRange(0.5, 1.2) * (RISK_PER_TRADE / qty);

                double exit = round(entry + move);

                /* -------- FIELDS -------- */

                trade.setId("T" + String.format("%04d", tradeCounter++));
                trade.setDate(tradeDate.toString());
                trade.setEntryTime(entryTime.toString());
                trade.setExitTime(exitTime.toString());

                trade.setSymbol(randomSymbol());
                trade.setQty(qty);
                trade.setEntry(entry);
                trade.setExit(exit);

                trade.setRisk(RISK_PER_TRADE);

                trades.add(trade);
            }
        }

        return trades;
    }

    /* ---------------- HELPERS ---------------- */

    private double randomPrice(double min, double max) {
        return round(min + random.nextDouble() * (max - min));
    }

    private double randomRange(double min, double max) {
        return min + random.nextDouble() * (max - min);
    }

    private int randomQty() {
        int[] values = {10, 15, 20, 25, 30};
        return values[random.nextInt(values.length)];
    }

    private String randomSymbol() {
        double r = random.nextDouble();
        if (r < 0.33) return "NIFTY";
        if (r < 0.66) return "BANKNIFTY";
        return "SENSEX";
    }

    private long randomDuration() {
        return (5 + random.nextInt(55)) * 60L;
    }

    private LocalTime randomTimeAfter(LocalTime after) {
        int start = after.toSecondOfDay();
        int end = MARKET_CLOSE.toSecondOfDay() - 300;

        if (start >= end) return after;

        int sec = start + random.nextInt(end - start);
        return LocalTime.ofSecondOfDay(sec);
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
