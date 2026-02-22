package com.example.find_my_edge.ast.util;

import com.example.find_my_edge.ast.context.SchemaType;
import com.example.find_my_edge.ast.util.parser.DateParser;
import com.example.find_my_edge.ast.util.parser.DateTimeParser;
import com.example.find_my_edge.ast.util.parser.DurationParser;
import com.example.find_my_edge.ast.util.parser.TimeParser;

import java.util.LinkedHashMap;
import java.util.Map;

public class DeformatUtil {
    private static final int MAX_SIZE = 1000;

    private static final Map<String, Object> CACHE =
            new LinkedHashMap<>(MAX_SIZE, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, Object> eldest) {
                    return size() > MAX_SIZE;
                }
            };

    public static Object parseWithCache(String value, SchemaType schemaType) {

        String key = schemaType.getSemanticType() + "|" +
                     schemaType.getFormat() + "|" +
                     value;

        if (CACHE.containsKey(key)) {
            return CACHE.get(key);
        }

        Object parsed = DeformatUtil.parse(value, schemaType);

        CACHE.put(key, parsed);
        return parsed;
    }

    public static Object parse(String raw, SchemaType schemaType) {

        if (raw == null) return null;

        String type = schemaType.getSemanticType();
        String format = schemaType.getFormat();

        return switch (type) {
            case "number" -> Double.parseDouble(raw);

            case "date" -> DateParser.parse(raw, format);

            case "time" -> TimeParser.parse(raw, format);

            case "duration" -> DurationParser.parse(raw, format);

            case "datetime" -> DateTimeParser.parse(raw, format);

            default -> raw;
        };
    }
}
