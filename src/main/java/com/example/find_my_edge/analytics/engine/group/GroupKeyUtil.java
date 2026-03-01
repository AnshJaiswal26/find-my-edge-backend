package com.example.find_my_edge.analytics.engine.group;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

public class GroupKeyUtil {

    private static final String EMPTY_KEY = "__EMPTY__";

    public static String getGroupKey(Object value) {
        if (value == null) return EMPTY_KEY;

        // ✅ Primitive / simple types
        if (!(value instanceof Map<?, ?> map)) {
            return String.valueOf(value);
        }

        Object typeObj = map.get("type");
        String type = typeObj != null ? typeObj.toString() : null;

        if (type == null) {
            return safeJsonFallback(map);
        }

        switch (type) {

            case "DATE_BUCKET":
                return buildKey(map, "unit", "key");

            case "TIME_BUCKET":
                return buildKey(map, "unit", "value");

            case "RANGE":
                return buildKey(map, "from", "to");

            default:
                return safeJsonFallback(map);
        }
    }

    // 🔹 Helper to safely build keys like unit_key, from_to
    private static String buildKey(Map<?, ?> map, String first, String second) {
        Object v1 = map.get(first);
        Object v2 = map.get(second);

        return String.valueOf(v1) + "_" + String.valueOf(v2);
    }

    // 🔹 Stable fallback (avoid random Map.toString ordering issues)
    private static String safeJsonFallback(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder("{");

        map.entrySet().stream()
           .sorted(Map.Entry.comparingByKey(Comparator.comparing(Object::toString)))
           .forEach(entry -> {
               sb.append(entry.getKey())
                 .append(":")
                 .append(Objects.toString(entry.getValue()))
                 .append(",");
           });

        if (sb.length() > 1) {
            sb.setLength(sb.length() - 1); // remove last comma
        }

        sb.append("}");
        return sb.toString();
    }
}