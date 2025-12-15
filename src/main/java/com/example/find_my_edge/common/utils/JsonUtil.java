package com.example.find_my_edge.common.utils;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

public final class JsonUtil {

    private JsonUtil() {
        throw new AssertionError("Utility Class");
    }

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String toJSON(List<String> list) {
        if (list == null) return null;
        try {
            return mapper.writeValueAsString(list);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert to JSON", e);
        }
    }

    public static <T> T fromJson(String json, TypeReference<T> type) {
        if (json == null) return null;
        try {
            return mapper.readValue(json, type);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert JSON to List", e);
        }
    }

}
