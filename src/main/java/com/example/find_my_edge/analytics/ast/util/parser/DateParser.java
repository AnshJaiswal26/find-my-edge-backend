package com.example.find_my_edge.analytics.ast.util.parser;


import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateParser {

    private static final Map<String, Integer> MONTHS_SHORT = Map.ofEntries(
            Map.entry("Jan", 0), Map.entry("Feb", 1), Map.entry("Mar", 2),
            Map.entry("Apr", 3), Map.entry("May", 4), Map.entry("Jun", 5),
            Map.entry("Jul", 6), Map.entry("Aug", 7), Map.entry("Sep", 8),
            Map.entry("Oct", 9), Map.entry("Nov", 10), Map.entry("Dec", 11)
    );

    private static final Map<String, Integer> MONTHS_LONG = Map.ofEntries(
            Map.entry("January", 0), Map.entry("February", 1), Map.entry("March", 2),
            Map.entry("April", 3), Map.entry("May", 4), Map.entry("June", 5),
            Map.entry("July", 6), Map.entry("August", 7), Map.entry("September", 8),
            Map.entry("October", 9), Map.entry("November", 10), Map.entry("December", 11)
    );

    private static final Map<String, String> TOKEN_MAP = Map.of(
            "YYYY", "(?<YYYY>\\d{4})",
            "MMMM", "(?<MMMM>[A-Za-z]+)",
            "MMM", "(?<MMM>[A-Za-z]{3})",
            "MM", "(?<MM>\\d{2})",
            "DD", "(?<DD>\\d{2})",
            "YY", "(?<YY>\\d{2})"
    );

    private static final List<String> TOKENS = TOKEN_MAP.keySet()
                                                        .stream()
                                                        .sorted((a, b) -> Integer.compare(b.length(), a.length()))
                                                        .toList();

    // Optional: cache compiled patterns
    private static final int MAX_CACHE_SIZE = 100;

    private static final Map<String, Pattern> PATTERN_CACHE =
            Collections.synchronizedMap(
                    new LinkedHashMap<>(MAX_CACHE_SIZE, 0.75f, true) {
                        @Override
                        protected boolean removeEldestEntry(Map.Entry<String, Pattern> eldest) {
                            return size() > MAX_CACHE_SIZE;
                        }
                    }
            );

    public static Long parse(String str, String format) {
        if (str == null || str.isBlank()) return null;

        Pattern pattern = PATTERN_CACHE.computeIfAbsent(format, DateParser::compilePattern);

        Matcher matcher = pattern.matcher(str.trim());
        if (!matcher.matches()) return null;

        Map<String, String> g = new HashMap<>();
        for (String token : TOKEN_MAP.keySet()) {
            try {
                String val = matcher.group(token);
                if (val != null) g.put(token, val);
            } catch (IllegalArgumentException ignored) {
            }
        }

        int year = resolveYear(g);
        int month = resolveMonth(g);
        int day = g.containsKey("DD") ? Integer.parseInt(g.get("DD")) : 1;

        try {
            LocalDate date = LocalDate.of(year, month + 1, day); // month+1 because Java = 1-based
            long epochDay = date.toEpochDay();
            return epochDay;
        } catch (Exception e) {
            return null;
        }
    }

    private static Pattern compilePattern(String format) {
        StringBuilder regex = new StringBuilder();
        int i = 0;

        while (i < format.length()) {
            boolean matched = false;

            for (String token : TOKENS) {
                if (format.startsWith(token, i)) {
                    regex.append(TOKEN_MAP.get(token));
                    i += token.length();
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                char c = format.charAt(i);
                regex.append(Pattern.quote(String.valueOf(c)));
                i++;
            }
        }

        return Pattern.compile("^" + regex + "$");
    }

    private static int resolveYear(Map<String, String> g) {
        if (g.containsKey("YYYY")) {
            return Integer.parseInt(g.get("YYYY"));
        }
        if (g.containsKey("YY")) {
            return 2000 + Integer.parseInt(g.get("YY"));
        }
        return LocalDate.now(ZoneOffset.UTC).getYear();
    }

    private static int resolveMonth(Map<String, String> g) {
        if (g.containsKey("MM")) {
            return Integer.parseInt(g.get("MM")) - 1;
        }
        if (g.containsKey("MMM")) {
            return MONTHS_SHORT.getOrDefault(g.get("MMM"), 0);
        }
        if (g.containsKey("MMMM")) {
            return MONTHS_LONG.getOrDefault(g.get("MMMM"), 0);
        }
        return 0;
    }
}