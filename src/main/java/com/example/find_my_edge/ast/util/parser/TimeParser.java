package com.example.find_my_edge.ast.util.parser;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeParser {

    private static final int MAX_CACHE_SIZE = 100;

    // LRU + thread-safe cache
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

        Pattern pattern = PATTERN_CACHE.computeIfAbsent(format, TimeParser::compilePattern);

        Matcher matcher = pattern.matcher(str.trim());
        if (!matcher.matches()) return null;

        int h = getGroupInt(matcher, "HH",
                            getGroupInt(matcher, "hh", 0));

        int m = getGroupInt(matcher, "mm", 0);
        int s = getGroupInt(matcher, "ss", 0);

        String ampm = getGroup(matcher, "A");

        if (ampm != null) {
            ampm = ampm.toUpperCase();

            if ("PM".equals(ampm) && h < 12) h += 12;
            if ("AM".equals(ampm) && h == 12) h = 0;
        }

        return h * 3600 + m * 60 + s;
    }

    private static Pattern compilePattern(String format) {

        String regex = format
                .replace("HH", "(?<HH>\\d{2})")
                .replace("hh", "(?<hh>\\d{2})")
                .replace("mm", "(?<mm>\\d{2})")
                .replace("ss", "(?<ss>\\d{2})")
                .replace("A", "(?<A>AM|PM)");

        return Pattern.compile("^" + regex + "$", Pattern.CASE_INSENSITIVE);
    }

    private static int getGroupInt(Matcher matcher, String group, int defaultVal) {
        try {
            String val = matcher.group(group);
            return val != null ? Integer.parseInt(val) : defaultVal;
        } catch (Exception e) {
            return defaultVal;
        }
    }

    private static String getGroup(Matcher matcher, String group) {
        try {
            return matcher.group(group);
        } catch (Exception e) {
            return null;
        }
    }
}