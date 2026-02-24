package com.example.find_my_edge.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JsonUtil {

    private final ObjectMapper objectMapper;

    /* ---------- WRITE ---------- */
    public String toJson(Object obj) {
        if (obj == null) return null;

        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("JSON write failed", e);
        }
    }

    /* ---------- READ OBJECT ---------- */
    public <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.isBlank()) return null;

        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("JSON read failed", e);
        }
    }

    /* ---------- READ LIST ---------- */
    public <T> List<T> fromJsonList(String json, Class<T> clazz) {
        if (json == null || json.isBlank()) return List.of();

        try {
            return objectMapper.readValue(
                    json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, clazz)
            );
        } catch (Exception e) {
            throw new RuntimeException("JSON list read failed", e);
        }
    }

    public <T> T copy(Object object, Class<T> clazz) {
        try {
            return objectMapper.readValue(
                    objectMapper.writeValueAsString(object),
                    clazz
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy SchemaDTO", e);
        }
    }
}