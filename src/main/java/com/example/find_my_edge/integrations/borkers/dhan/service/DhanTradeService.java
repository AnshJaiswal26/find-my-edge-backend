package com.example.find_my_edge.integrations.borkers.dhan.service;

import com.example.find_my_edge.integrations.borkers.dhan.config.DhanConfig;
import com.example.find_my_edge.integrations.borkers.dhan.dto.DhanTradeDto;
import com.example.find_my_edge.integrations.borkers.dhan.dto.DhanTradeResponseDto;
import com.example.find_my_edge.integrations.borkers.dhan.exception.NoTradesFound;
import com.example.find_my_edge.integrations.borkers.dhan.exception.TradeFetchFailed;
import com.example.find_my_edge.integrations.borkers.dhan.model.ProcessedTrade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DhanTradeService {

    private final RestClient restClient;
    private final DhanOAuthService oauthService;
    private final DhanConfig config;

    public List<ProcessedTrade> getTrades(String fromDate, String toDate, int page) {

        String token = oauthService.getValidToken();

        String url = config.getBaseUrl() +
                     "/v2/trades/" + fromDate + "/" + toDate + "/" + page;

        DhanTradeResponseDto dhanTradeResponseDto =
                restClient.get()
                          .uri(url)
                          .headers(headers -> {
                              headers.set("access-token", token);
                              headers.set("Content-Type", "application/json");
                          })
                          .retrieve()
                          .onStatus(
                                  HttpStatusCode::isError, (req, res) -> {
                                      throw new TradeFetchFailed("Dhan API failed");
                                  }
                          )
                          .body(DhanTradeResponseDto.class);

        if (dhanTradeResponseDto == null
            || dhanTradeResponseDto.getData() == null
            || dhanTradeResponseDto.getData().isEmpty()
        ) {
            throw new NoTradesFound("No trade data found");
        }

        return processTrades(dhanTradeResponseDto.getData());
    }

    public List<ProcessedTrade> processTrades(List<DhanTradeDto> trades) {

        Map<String, List<DhanTradeDto>> grouped =
                trades.stream().filter(trade -> trade.getInstrument().equals("OPTIDX"))
                      .collect(Collectors.groupingBy(DhanTradeDto::getOrderId));

        if (grouped.isEmpty()) {
            return Collections.emptyList();
        }

        List<ProcessedTrade> result = new ArrayList<>();

        for (Map.Entry<String, List<DhanTradeDto>> entry : grouped.entrySet()) {

            List<DhanTradeDto> group = entry.getValue();

            DhanTradeDto buy = null;
            DhanTradeDto sell = null;

            double buyCharges = 0;
            double sellCharges = 0;

            String date = "";

            for (DhanTradeDto t : group) {
                if ("BUY".equalsIgnoreCase(t.getTransactionType())) {

                    date = t.getExchangeTime().split("T")[0];

                    buy = t;
                    buyCharges += t.getBrokerageCharges() +
                                  t.getServiceTax() +
                                  t.getSebiTax() +
                                  t.getStt() +
                                  t.getExchangeTransactionCharges() +
                                  t.getStampDuty();

                } else if ("SELL".equalsIgnoreCase(t.getTransactionType())) {
                    sell = t;

                    sellCharges += t.getBrokerageCharges() +
                                   t.getServiceTax() +
                                   t.getSebiTax() +
                                   t.getStt() +
                                   t.getExchangeTransactionCharges() +
                                   t.getStampDuty();
                }
            }

            if (buy != null && sell != null) {

                double charges = buyCharges + sellCharges;

                double pnl = (sell.getTradedPrice() - buy.getTradedPrice())
                             * buy.getTradedQuantity();

                result.add(
                        ProcessedTrade.builder()
                                      .orderId(buy.getOrderId())
                                      .date(date)
                                      .symbol(buy.getCustomSymbol())
                                      .strikePrice(buy.getDrvStrikePrice())
                                      .direction(buy.getDrvOptionType())
                                      .quantity(buy.getTradedQuantity())
                                      .buyPrice(buy.getTradedPrice())
                                      .sellPrice(sell.getTradedPrice())
                                      .pnl(pnl)
                                      .charges(charges)
                                      .entryTime(buy.getExchangeTime())
                                      .exitTime(sell.getExchangeTime())
                                      .build()
                );
            }
        }

        return result;
    }
}