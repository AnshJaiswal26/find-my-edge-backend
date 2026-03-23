package com.example.find_my_edge.trade_setup.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class TradeSetupCollectionResponse {

    private Map<String, TradeSetupResponse> tradeSetupsById;

    private List<String> tradeSetupsOrder;
}
