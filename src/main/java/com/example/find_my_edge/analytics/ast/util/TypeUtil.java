package com.example.find_my_edge.analytics.ast.util;

public class TypeUtil {

    public static Double toDouble(Object val) {
        return ((Number) val).doubleValue();
    }

    public static boolean toBoolean(Object val) {
        if (val instanceof Number) {
            return ((Number) val).doubleValue() != 0;
        }
        return Boolean.TRUE.equals(val);
    }
}