package com.example.find_my_edge.trade_setup.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;


@Data
public class TradeSetupRequest {

    private String name;
    private String imageUrl;
    private String imagePublicId;

    private List<String> fieldOrder;

    private Map<String, SetupFieldRequest> fieldsById;
}