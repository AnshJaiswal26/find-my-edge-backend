package com.example.find_my_edge.trade.mapper;

import com.example.find_my_edge.integrations.borkers.dhan.model.ProcessedTrade;
import com.example.find_my_edge.trade.model.Trade;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Map;

@Component
public class ProcessedTradeMapper {

    public Trade mapToTrade(ProcessedTrade t) {

        Trade trade = new Trade();

        trade.setExternalId(t.getOrderId()); // 🔥 important

        Map<String, Object> v = trade.getValues();

        /* -------- DATE -------- */
        long dateEpoch = LocalDate.parse(t.getEntryTime().substring(0, 10))
                                  .atStartOfDay(ZoneOffset.UTC)
                                  .toEpochSecond();

        v.put("date", dateEpoch);

        /* -------- TIME -------- */
        LocalTime entry = LocalTime.parse(t.getEntryTime().substring(11));
        LocalTime exit = LocalTime.parse(t.getExitTime().substring(11));

        v.put("entryTime", entry.toSecondOfDay());
        v.put("exitTime", exit.toSecondOfDay());

        v.put("duration", exit.toSecondOfDay() - entry.toSecondOfDay());

        /* -------- TRADE INFO -------- */
        v.put("symbol", t.getSymbol());
        v.put("qty", t.getQuantity());

        v.put("entry", t.getBuyPrice());
        v.put("exit", t.getSellPrice());

        /* -------- PNL -------- */
        v.put("pnl", t.getPnl());

        v.put("charges", t.getCharges());

        /* -------- DIRECTION -------- */
        v.put("direction", t.getDirection()); // PUT / CALL

        return trade;
    }
}
