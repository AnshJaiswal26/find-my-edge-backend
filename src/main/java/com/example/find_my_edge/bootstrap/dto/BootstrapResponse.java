package com.example.find_my_edge.bootstrap.dto;

import com.example.find_my_edge.schema.dto.SchemaResponse;
import com.example.find_my_edge.trade_setup.dto.TradeSetupResponse;
import com.example.find_my_edge.trade_setup.model.EvaluationResult;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class BootstrapResponse {

    private Map<String, SchemaResponse> schemasById;

    private List<String> schemasOrder;

    private Map<String, Map<String, Object>> tradesById;

    private Map<String, Map<String, Object>> derivedByTradeId;

    private List<String> tradesOrder;

    private List<String> tradeSetupsOrder;

    private Map<String, TradeSetupResponse> tradeSetupsById;

    Map<String, Map<String, EvaluationResult>> setupScoreResult;
}
