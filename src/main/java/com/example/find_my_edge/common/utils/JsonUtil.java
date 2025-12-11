package com.example.find_my_edge.common.utils;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String toJSON(List<String> list) {
        try {
            return mapper.writeValueAsString(list);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert to JSON", e);
        }
    }

    public static List<String> toList(String json) {
        try {
            return mapper.readValue(
                    json, new TypeReference<List<String>>() {
                    }
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert JSON to List", e);
        }
    }

    public static Map<String, Object> toMap(String json) {
        try {
            return mapper.readValue(
                    json, new TypeReference<Map<String, Object>>() {
                    }
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert JSON to List", e);
        }
    }
}
