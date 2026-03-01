package com.example.find_my_edge.domain.schema.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SchemaSource {
    SYSTEM, USER, COMPUTED;

    // SYSTEM and SYSTEM_REQUIRED
    // SYSTEM and SYSTEM_OPTIONAL
    // USER and USER_DEFINED
    // COMPUTED and SYSTEM_REQUIRED
    // COMPUTED and USER_DEFINED
    // COMPUTED and SYSTEM_OPTIONAL



    // SYSTEM and USER_DEFINED
    // USER and SYSTEM_REQUIRED
    // USER and SYSTEM_OPTIONAL



    @JsonValue
    public String toJson() {
        return this.name().toLowerCase();
    }

    @JsonCreator
    public static SchemaSource fromJson(String value) {
        return SchemaSource.valueOf(value.toUpperCase());
    }
}
