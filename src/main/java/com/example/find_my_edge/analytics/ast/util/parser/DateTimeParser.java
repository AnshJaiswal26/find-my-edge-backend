package com.example.find_my_edge.analytics.ast.util.parser;


public class DateTimeParser {

    public static Long parse(String str, String format) {
        if (str == null || str.isBlank()) return null;

        str = str.trim();

        // Split input into parts
        String[] parts = str.split("\\s+");

        if (parts.length < 2) return null;

        // Split into date + time
        String datePart = parts[0];
        String timePart = String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length));

        // Split format into date + time format
        String[] formatParts = format.split("\\s+", 2);

        if (formatParts.length < 2) return null;

        String dateFormat = formatParts[0];
        String timeFormat = formatParts[1];

        Long days = DateParser.parse(datePart, dateFormat);
        Integer seconds = TimeParser.parse(timePart, timeFormat);

        if (days == null || seconds == null) return null;

        return days * 86400 + seconds;
    }
}