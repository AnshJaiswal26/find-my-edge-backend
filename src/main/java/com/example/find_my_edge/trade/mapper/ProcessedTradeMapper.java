package com.example.find_my_edge.trade.mapper;

import com.example.find_my_edge.integrations.borkers.common.model.ProcessedTrade;
import com.example.find_my_edge.trade.model.Trade;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;

@Component
public class ProcessedTradeMapper {

    public Trade mapToTrade(ProcessedTrade t) {

        Trade trade = new Trade();

        trade.setExternalId(t.getOrderId()); // important

        /* -------- DATE -------- */
        long dateEpoch = LocalDate.parse(t.getEntryTime().substring(0, 10))
                                  .atStartOfDay(ZoneOffset.UTC)
                                  .toEpochSecond();

        trade.setDate(dateEpoch);

        /* -------- TIME -------- */
        LocalTime entry = LocalTime.parse(t.getEntryTime().substring(11));
        LocalTime exit = LocalTime.parse(t.getExitTime().substring(11));

        trade.setEntryTime(entry.toSecondOfDay());
        trade.setExitTime(exit.toSecondOfDay());

        /* -------- TRADE INFO -------- */
        trade.setSymbol(t.getSymbol());
        trade.setQty(t.getQuantity());

        trade.setEntryPrice(t.getBuyPrice());
        trade.setExitPrice(t.getSellPrice());

        trade.setCharges(t.getCharges());
        trade.setDirection(t.getDirection());

        return trade;
    }
}
