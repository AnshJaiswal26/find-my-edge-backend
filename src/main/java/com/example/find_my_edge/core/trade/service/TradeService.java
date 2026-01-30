package com.example.find_my_edge.core.trade.service;

import com.example.find_my_edge.core.trade.dto.Trade;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class TradeService {

    private static final int DAYS = 60;
    private static final double INITIAL_CAPITAL = 15000;
    private static final double RISK = 500;

    private static final LocalTime MARKET_OPEN = LocalTime.of(9, 15);
    private static final LocalTime MARKET_CLOSE = LocalTime.of(15, 30);

    private final Random random = new Random();

    public List<Trade> getTrades() {
        List<Trade> trades = new ArrayList<>();

        double cumulativePnl = 0;
        double capital = INITIAL_CAPITAL;

        LocalDate startDate = LocalDate.now().minusDays(DAYS);
        int tradeCounter = 1;

        for (int d = 0; d < DAYS; d++) {
            LocalDate tradeDate = startDate.plusDays(d);

            // 🔥 1–5 trades per day
            int tradesToday = 1 + random.nextInt(5);

            LocalTime lastExitTime = MARKET_OPEN;

            for (int t = 0; t < tradesToday; t++) {
                Trade trade = new Trade();

                // --- Time generation ---
                LocalTime entryTime = randomTimeAfter(lastExitTime);
                long durationSeconds = randomDuration();
                LocalTime exitTime = entryTime.plusSeconds(durationSeconds);

                if (exitTime.isAfter(MARKET_CLOSE)) {
                    exitTime = MARKET_CLOSE.minusMinutes(20L);
                    entryTime = entryTime.minusMinutes(40L);
                }

                lastExitTime = exitTime;

                // --- Price logic ---
                double entry = randomPrice(100, 400);
                int qty = randomQty();
                double exit = entry + randomPrice(-RISK / qty, 100);

                double pnl = round((exit - entry) * qty);
                cumulativePnl = round(cumulativePnl + pnl);
                capital = round(capital + pnl);

                trade.setTradeId("T" + String.format("%04d", tradeCounter++));
                trade.setDate(tradeDate.toString());
                trade.setEntryTime(entryTime.toString());
                trade.setExitTime(exitTime.toString());

                trade.setSymbol(randomSymbol());
                trade.setQty(qty);
                trade.setEntry(entry);
                trade.setExit(exit);
                trade.setPnl(pnl);
                trade.setCumulativePnl(cumulativePnl);
                trade.setCapital(capital);
                trade.setRisk(RISK);
                trade.setRr(round(pnl / RISK));
                trade.setCharges(round(65 + random.nextDouble() * 10));
                trade.setDuration(durationSeconds);

                trades.add(trade);
            }
        }

        return trades;
    }

    /* ---------------- helpers ---------------- */

    private double randomPrice(double min, double max) {
        return round(min + random.nextDouble() * (max - min));
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
        // 5–60 minutes
        return (5 + random.nextInt(55)) * 60L;
    }

    private LocalTime randomTimeAfter(LocalTime after) {
        int startSecond = after.toSecondOfDay();
        int endSecond = MARKET_CLOSE.toSecondOfDay() - 300; // buffer

        if (startSecond >= endSecond) {
            return after;
        }

        int randomSecond = startSecond + random.nextInt(endSecond - startSecond);
        return LocalTime.ofSecondOfDay(randomSecond);
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
