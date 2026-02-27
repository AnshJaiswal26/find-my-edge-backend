package com.example.find_my_edge.workspace.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PageType {

    DASHBOARD("dashboard"),
    SHEET_INTEGRATION("sheet-integration"),
    TRADE_METRIC("trade-metric"),
    TRADE_SETUPS("trade-setups");

    private final String key;

    PageType(String key) {
        this.key = key;
    }

    @JsonValue
    public String toJson() {
        return key;
    }

    public String key(){
        return key;
    }

    @JsonCreator
    public static PageType fromJson(String value) {
        for (PageType pageType : PageType.values()) {
            if (pageType.key.equalsIgnoreCase(value)) {
                return pageType;
            }
        }
        throw new IllegalArgumentException("Invalid Page: " + value);
    }
}

