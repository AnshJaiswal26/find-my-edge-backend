package com.example.find_my_edge.integrations.borkers.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Broker {
    DHAN,
    ZERODHA,
    ANGEL_ONE;

    @JsonValue
    public String toJson() {
        return this.toString().toLowerCase();
    }

    @JsonCreator
    public static Broker fromJson(String value) {
        return Broker.valueOf(value.toUpperCase());
    }

    public String getName() {
        return this.toString().toLowerCase();
    }
}