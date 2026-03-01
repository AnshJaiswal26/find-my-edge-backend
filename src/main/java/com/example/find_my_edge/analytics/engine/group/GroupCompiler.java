package com.example.find_my_edge.analytics.engine.group;

import com.example.find_my_edge.analytics.config.FilterConfig;
import com.example.find_my_edge.analytics.config.GroupConfig;
import com.example.find_my_edge.analytics.config.GroupRangeConfig;
import com.example.find_my_edge.analytics.engine.filter.FilterOperation;
import com.example.find_my_edge.analytics.engine.filter.FilterOperationRegistry;
import com.example.find_my_edge.domain.trade.model.Trade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.*;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class GroupCompiler {

    private final FilterOperationRegistry registry;

    public Function<Trade, Object> compile(
            GroupConfig spec,
            BiFunction<Trade, String, Object> getValue
    ) {

        if (spec == null || spec.getType() == null) {
            throw new IllegalArgumentException("Group spec/type cannot be null");
        }

        switch (spec.getType()) {

            case "value":
                return trade -> getValue.apply(trade, spec.getKey());

            case "dateBucket":
                return trade -> getDateBucket(
                        getValue.apply(trade, spec.getKey()),
                        spec.getUnit()
                );

            case "timeBucket":
                return trade -> getTimeBucket(
                        getValue.apply(trade, spec.getKey()),
                        spec.getUnit()
                );

            case "numberRange":
                return trade -> matchRange(
                        safeNumber(getValue.apply(trade, spec.getKey())),
                        spec.getRanges()
                );

            case "timeRange":
                return trade -> matchRange(
                        safeComparable(getValue.apply(trade, spec.getKey())),
                        spec.getRanges()
                );

            case "condition":
                return trade -> {
                    Object fieldValue = getValue.apply(trade, spec.getKey());
                    FilterOperation op = registry.get(spec.getOperator());

                    boolean result = op.apply(
                            fieldValue,
                            new FilterConfig(
                                    (Double) spec.getValue(),
                                    spec.getFrom(),
                                    spec.getTo()
                            )
                    );

                    return result
                           ? spec.getLabels().getOrDefault("match", "Match")
                           : spec.getLabels().getOrDefault("nonMatch", "Other");
                };

            default:
                throw new IllegalArgumentException("Unknown group type: " + spec.getType());
        }
    }

    private Long toEpochSeconds(Object value) {
        if (value == null) return null;

        if (value instanceof Number n) {
            long v = n.longValue();

            // Heuristic detection
            if (v > 1_000_000_000_000L) {
                // looks like milliseconds
                return v / 1000;
            }

            if (v < 1_000_000_000L) {
                // could be days (very small)
                // optional: only if your system ever sends days
                return v * 86400;
            }

            // already seconds
            return v;
        }

        // Handle Java time types
        if (value instanceof Instant i) {
            return i.getEpochSecond();
        }

        if (value instanceof LocalDateTime dt) {
            return dt.toEpochSecond(ZoneOffset.UTC);
        }

        if (value instanceof LocalDate d) {
            return d.atStartOfDay(ZoneOffset.UTC).toEpochSecond();
        }

        return null;
    }

    // =========================
    // 🔹 DATE BUCKET
    // =========================

    private Object getDateBucket(Object value, String unit) {
        if (value == null || unit == null) return null;

        Long epochSeconds = toEpochSeconds(value);
        if (epochSeconds == null) return null;

        // ✅ Always use UTC (critical)
        Instant instant = Instant.ofEpochSecond(epochSeconds);
        ZonedDateTime zdt = instant.atZone(ZoneOffset.UTC);

        int year = zdt.getYear();
        int month = zdt.getMonthValue() - 1; // JS-style (0-based)

        switch (unit) {

            case "day": {
                long dayStart = zdt.toLocalDate()
                                   .atStartOfDay(ZoneOffset.UTC)
                                   .toEpochSecond();

                return Map.of(
                        "type", "DATE_BUCKET",
                        "unit", "day",
                        "key", dayStart,     // grouping key
                        "value", dayStart    // actual value
                );
            }

            case "month": {
                ZonedDateTime monthStart = ZonedDateTime.of(
                        year, month + 1, 1,
                        0, 0, 0, 0,
                        ZoneOffset.UTC
                );

                long monthStartSec = monthStart.toEpochSecond();

                return Map.of(
                        "type", "DATE_BUCKET",
                        "unit", "month",
                        "key", year * 12 + month,  // same as JS
                        "value", monthStartSec,
                        "year", year,
                        "month", month
                );
            }

            case "year": {
                ZonedDateTime yearStart = ZonedDateTime.of(
                        year, 1, 1,
                        0, 0, 0, 0,
                        ZoneOffset.UTC
                );

                long yearStartSec = yearStart.toEpochSecond();

                return Map.of(
                        "type", "DATE_BUCKET",
                        "unit", "year",
                        "key", year,
                        "value", yearStartSec,
                        "year", year
                );
            }

            case "week": {
                WeekFields wf = WeekFields.ISO;

                int week = zdt.get(wf.weekOfWeekBasedYear());
                int weekYear = zdt.get(wf.weekBasedYear());

                ZonedDateTime weekStart = zdt
                        .with(wf.dayOfWeek(), 1) // Monday
                        .toLocalDate()
                        .atStartOfDay(ZoneOffset.UTC);

                long weekStartSec = weekStart.toEpochSecond();

                return Map.of(
                        "type", "DATE_BUCKET",
                        "unit", "week",
                        "key", weekYear * 100 + week,
                        "value", weekStartSec,
                        "year", weekYear,
                        "week", week
                );
            }

            default:
                return Map.of(
                        "type", "DATE_BUCKET",
                        "unit", "raw",
                        "key", epochSeconds,
                        "value", epochSeconds
                );
        }
    }

    // =========================
    // 🔹 TIME BUCKET
    // =========================

    private Integer toMinutes(Object value) {
        if (value == null) return null;

        if (value instanceof Number n) {
            return n.intValue();
        }

        return null;
    }

    private Object getTimeBucket(Object value, String unit) {

        if (value == null) {
            return Map.of("type", "EMPTY");
        }

        Integer minutes = toMinutes(value);
        if (minutes == null) {
            return Map.of("type", "EMPTY");
        }

        if ("hour".equals(unit)) {
            int hour = minutes / 60; // same as Math.floor

            return Map.of(
                    "type", "TIME_BUCKET",
                    "unit", "hour",
                    "value", hour   // 0–23
            );
        }

        return Map.of("type", "OTHER");
    }

    // =========================
    // 🔹 RANGE CHECK
    // =========================

    @SuppressWarnings({"unchecked", "rawtypes"})
    private boolean inRange(Comparable value, Object from, Object to) {

        boolean gte = (from == null) || value.compareTo(from) >= 0;
        boolean lte = (to == null) || value.compareTo(to) <= 0;

        return gte && lte;
    }

    private Object matchRange(Object value, List<GroupRangeConfig> ranges) {

        if (value == null) {
            return Map.of("type", "EMPTY");
        }

        if (!(value instanceof Comparable<?> comparable)) {
            return Map.of("type", "OTHER");
        }

        if (ranges == null || ranges.isEmpty()) {
            return Map.of("type", "OTHER");
        }

        for (GroupRangeConfig r : ranges) {
            if (r == null) continue;

            Object from = r.getFrom();
            Object to = r.getTo();

            if (inRange(comparable, from, to)) {
                return Map.of(
                        "type", "RANGE",
                        "from", from,
                        "to", to
                );
            }
        }

        return Map.of("type", "OTHER");
    }

    // =========================
    // 🔹 SAFE CAST HELPERS
    // =========================
    private Number safeNumber(Object val) {
        return (val instanceof Number n) ? n : null;
    }

    private Comparable<?> safeComparable(Object val) {
        return (val instanceof Comparable<?> c) ? c : null;
    }

}