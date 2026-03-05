package com.example.find_my_edge.integrations.borkers.dhan.service;

import com.example.find_my_edge.integrations.borkers.dhan.config.DhanConfig;
import com.example.find_my_edge.integrations.borkers.dhan.dto.DhanTradeResponseDto;
import com.example.find_my_edge.integrations.borkers.common.exception.NoTradesFoundException;
import com.example.find_my_edge.integrations.borkers.common.exception.TradeFetchFailedException;
import com.example.find_my_edge.integrations.borkers.common.model.ProcessedTrade;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DhanTradeService {

    private final RestClient restClient;
    private final DhanOAuthService oauthService;
    private final DhanConfig config;

    public List<ProcessedTrade> getTrades(LocalDate fromDate, LocalDate toDate, int page) {

        String token = oauthService.getValidToken();

        String validatedFrom = fromDate.toString();
        String validatedTo = toDate.toString();

        String url = config.getBaseUrl() +
                     "/v2/trades/" + validatedFrom + "/" + validatedTo + "/" + page;

        List<DhanTradeResponseDto> trades =
                restClient.get()
                          .uri(url)
                          .headers(headers -> {
                              headers.set("access-token", token);
                              headers.set("Content-Type", "application/json");
                          })
                          .retrieve()
                          .onStatus(
                                  HttpStatusCode::isError,
                                  (req, res) -> {
                                      throw new TradeFetchFailedException("Dhan API failed");
                                  }
                          )
                          .body(new ParameterizedTypeReference<List<DhanTradeResponseDto>>() {
                          });

        if (trades == null || trades.isEmpty()
        ) {
            throw new NoTradesFoundException("No trade data found");
        }

        return processTrades(trades);
    }

    public List<ProcessedTrade> processTrades(List<DhanTradeResponseDto> trades) {

        Map<String, List<DhanTradeResponseDto>> grouped =
                trades.stream().filter(trade -> trade.getInstrument().equals("OPTIDX"))
                      .collect(Collectors.groupingBy(DhanTradeResponseDto::getOrderId));

        if (grouped.isEmpty()) {
            return Collections.emptyList();
        }

        List<ProcessedTrade> result = new ArrayList<>();

        for (Map.Entry<String, List<DhanTradeResponseDto>> entry : grouped.entrySet()) {

            List<DhanTradeResponseDto> group = entry.getValue();

            DhanTradeResponseDto buy = null;
            DhanTradeResponseDto sell = null;

            double buyCharges = 0;
            double sellCharges = 0;

            String date = "";

            for (DhanTradeResponseDto t : group) {
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