package com.example.find_my_edge.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


public enum Page {

    DASHBOARD("dashboard"),
    SHEET_INTEGRATION("sheet-integration"),
    TRADE_METRIC("trade-metric"),
    TRADE_SETUPS("trade-setups");

    private final String key;

    Page(String key) {
        this.key = key;
    }

    @JsonValue
    public String toJson() {
        return key;
    }

    @JsonCreator
    public static Page fromJson(String value) {
        for (Page page : Page.values()) {
            if (page.key.equalsIgnoreCase(value)) {
                return page;
            }
        }
        throw new IllegalArgumentException("Invalid Page: " + value);
    }
}