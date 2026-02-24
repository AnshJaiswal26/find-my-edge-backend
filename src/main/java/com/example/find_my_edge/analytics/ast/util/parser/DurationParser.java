package com.example.find_my_edge.analytics.ast.util.parser;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DurationParser {

    private static final int MAX_CACHE_SIZE = 100;

    // LRU cache for compiled patterns
    private static final Map<String, Pattern> PATTERN_CACHE =
            Collections.synchronizedMap(
                    new LinkedHashMap<>(MAX_CACHE_SIZE, 0.75f, true) {
                        @Override
                        protected boolean removeEldestEntry(Map.Entry<String, Pattern> eldest) {
                            return size() > MAX_CACHE_SIZE;
                        }
                    }
            );

    public static Integer parse(String str, String format) {
        if (str == null || str.isBlank()) return null;

        str = str.trim();

        /* ---------- HUMAN FORMATS ---------- */

        if ("human".equals(format) || "human-short".equals(format)) {
            return parseHuman(str);
        }

        if ("human-min".equals(format)) {
            return parseHumanMin(str);
        }

        /* ---------- CLASSIC FORMATS ---------- */

        Pattern pattern = PATTERN_CACHE.computeIfAbsent(format, DurationParser::compilePattern);

        Matcher matcher = pattern.matcher(str);
        if (!matcher.matches()) return null;

        int days = getGroupInt(matcher, "DD", 0);
        int hours = getGroupInt(
                matcher, "HH",
                getGroupInt(matcher, "hh", 0)
        );
        int minutes = getGroupInt(matcher, "mm", 0);
        int seconds = getGroupInt(matcher, "ss", 0);

        return days * 86400 + hours * 3600 + minutes * 60 + seconds;
    }

    /* ---------- HUMAN PARSERS ---------- */

    private static Integer parseHuman(String str) {
        Pattern pattern = Pattern.compile(
                "(?:(\\d+)d)?\\s*(?:(\\d+)h)?\\s*(?:(\\d+)m)?\\s*(?:(\\d+)s)?",
                Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = pattern.matcher(str);
        if (!matcher.matches()) return null;

        int d = parseIntSafe(matcher.group(1));
        int h = parseIntSafe(matcher.group(2));
        int m = parseIntSafe(matcher.group(3));
        int s = parseIntSafe(matcher.group(4));

        return d * 86400 + h * 3600 + m * 60 + s;
    }

    private static Integer parseHumanMin(String str) {
        Pattern pattern = Pattern.compile("^(\\d+)m$", Pattern.CASE_INSENSITIVE);

        Matcher matcher = pattern.matcher(str);
        if (!matcher.matches()) return null;

        int minutes = Integer.parseInt(matcher.group(1));
        return minutes * 60;
    }

    /* ---------- FORMAT COMPILER ---------- */

    private static Pattern compilePattern(String format) {
        String regex = format
                .replace("DD", "(?<DD>\\d{2})")
                .replace("HH", "(?<HH>\\d{2})")
                .replace("hh", "(?<hh>\\d{2})")
                .replace("mm", "(?<mm>\\d{2})")
                .replace("ss", "(?<ss>\\d{2})");

        return Pattern.compile("^" + regex + "$");
    }

    /* ---------- HELPERS ---------- */

    private static int getGroupInt(Matcher matcher, String group, int defaultVal) {
        try {
            String val = matcher.group(group);
            return val != null ? Integer.parseInt(val) : defaultVal;
        } catch (Exception e) {
            return defaultVal;
        }
    }

    private static int parseIntSafe(String val) {
        return val != null ? Integer.parseInt(val) : 0;
    }
}