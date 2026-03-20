package com.example.find_my_edge.trade_setup.model;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class TradeSetup {

    private String id;

    private UUID userId;

    private String name;

    private String imageUrl;
    private String imagePublicId;

    // Keep as List (already good for ordering)
    private List<String> fieldOrder;

    // Better for computation than List
    private Map<String, SetupField> fieldsById;
}