package com.example.find_my_edge.common.util;

import com.example.find_my_edge.common.exceptions.JsonConversionException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
            throw new JsonConversionException("JSON write failed", e);
        }
    }

    /* ---------- READ OBJECT ---------- */
    public <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.isBlank()) return null;

        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new JsonConversionException("JSON read failed", e);
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
            throw new JsonConversionException("JSON list read failed", e);
        }
    }

    /* ---------- WRITE LIST ---------- */
    public String toJsonList(List<?> list) {

        if (list == null || list.isEmpty()) return "[]";

        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            throw new JsonConversionException("JSON list write failed", e);
        }
    }

    /* ---------- COPY LIST ---------- */
    public <T> List<T> fromList(List<?> source, Class<T> clazz) {

        if (source == null || source.isEmpty()) return List.of();

        try {
            return source.stream()
                         .map(item -> objectMapper.convertValue(item, clazz))
                         .toList();

        } catch (Exception e) {
            throw new JsonConversionException("JSON list copy failed", e);
        }
    }

    public <T> T copy(Object object, Class<T> clazz) {
        try {
            return objectMapper.readValue(
                    objectMapper.writeValueAsString(object),
                    clazz
            );
        } catch (Exception e) {
            throw new JsonConversionException("Failed to copy SchemaDTO", e);
        }
    }

    public String pretty(Object json){
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}