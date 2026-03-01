package com.example.find_my_edge.analytics.engine.filter;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FilterOperationRegistry {

    private final Map<String, FilterOperation> operations = new HashMap<>();

    public FilterOperationRegistry() {

        // TEXT
        operations.put("textContains", (v, f) ->
                v != null && v.toString().toLowerCase()
                              .contains(f.getValue().toString().toLowerCase())
        );

        operations.put("textIsExactly", (v, f) ->
                v != null && v.toString().equalsIgnoreCase(f.getValue().toString())
        );

        // NUMBER
        operations.put("greaterThan", (v, f) ->
                ((Number) v).doubleValue() > ((Number) f.getValue()).doubleValue()
        );

        operations.put("lessThan", (v, f) ->
                ((Number) v).doubleValue() < ((Number) f.getValue()).doubleValue()
        );

        operations.put("isBetween", (v, f) ->
                ((Number) v).doubleValue() >= ((Number) f.getFrom()).doubleValue()
                && ((Number) v).doubleValue() <= ((Number) f.getTo()).doubleValue()
        );

        operations.put("isNotBetween", (v, f) ->
                ((Number) v).doubleValue() < ((Number) f.getFrom()).doubleValue()
                || ((Number) v).doubleValue() > ((Number) f.getTo()).doubleValue()
        );

        // DEFAULT
        operations.put("none", (v, f) -> true);
    }

    public FilterOperation get(String operator) {
        return operations.getOrDefault(operator, operations.get("none"));
    }
}