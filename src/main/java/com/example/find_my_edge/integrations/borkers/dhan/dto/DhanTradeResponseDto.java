package com.example.find_my_edge.integrations.borkers.dhan.dto;

import lombok.Data;

@Data
public class DhanTradeResponseDto {

    private String orderId;
    private String exchangeOrderId;
    private String exchangeTradeId;
    private String transactionType;
    private String exchangeSegment;
    private String productType;
    private String orderType;
    private String customSymbol;
    private String securityId;
    private Integer tradedQuantity;
    private Double tradedPrice;
    private String instrument;
    private Double sebiTax;
    private Double stt;
    private Double brokerageCharges;
    private Double serviceTax;
    private Double exchangeTransactionCharges;
    private Double stampDuty;
    private String createTime;
    private String updateTime;
    private String exchangeTime;
    private String drvExpiryDate;
    private String drvOptionType;
    private Double drvStrikePrice;

}