package com.example.find_my_edge.analytics.engine.filter;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FilterOperationRegistry {

    private final Map<String, FilterOperation> operations = new HashMap<>();

    public FilterOperationRegistry() {

        /* ================= DEFAULT ================= */

        operations.put("none", (v, f) -> true);

        /* ================= TEXT ================= */

        FilterOperation contains =
                (v, f) -> str(v).contains(str(f.getValue()));

        FilterOperation notContains =
                (v, f) -> !str(v).contains(str(f.getValue()));

        FilterOperation startsWith =
                (v, f) -> str(v).startsWith(str(f.getValue()));

        FilterOperation endsWith =
                (v, f) -> str(v).endsWith(str(f.getValue()));

        FilterOperation equalsText =
                (v, f) -> str(v).equals(str(f.getValue()));

        operations.put("textContains", contains);
        operations.put("textDoesNotContain", notContains);
        operations.put("textStartsWith", startsWith);
        operations.put("textEndsWith", endsWith);
        operations.put("textIsExactly", equalsText);


        /* ================= NUMERIC COMPARATORS ================= */
        /* used by NUMBER, DATE, TIME, DURATION */

        FilterOperation gt =
                (v, f) -> num(v) > num(f.getValue());

        FilterOperation gte =
                (v, f) -> num(v) >= num(f.getValue());

        FilterOperation lt =
                (v, f) -> num(v) < num(f.getValue());

        FilterOperation lte =
                (v, f) -> num(v) <= num(f.getValue());

        FilterOperation eq =
                (v, f) -> num(v) == num(f.getValue());

        FilterOperation neq =
                (v, f) -> num(v) != num(f.getValue());

        FilterOperation between =
                (v, f) -> num(v) >= f.getFrom() && num(v) <= f.getTo();

        FilterOperation notBetween =
                (v, f) -> num(v) < f.getFrom() || num(v) > f.getTo();


        /* ================= NUMBER ================= */

        operations.put("greaterThan", gt);
        operations.put("greaterThanEqualTo", gte);
        operations.put("lessThan", lt);
        operations.put("lessThanEqualTo", lte);
        operations.put("isEqualTo", eq);
        operations.put("isNotEqualTo", neq);
        operations.put("isBetween", between);
        operations.put("isNotBetween", notBetween);


        /* ================= DATE ================= */

        operations.put("dateIs", eq);
        operations.put("dateBefore", lt);
        operations.put("dateAfter", gt);
        operations.put("dateBetween", between);
        operations.put("dateNotBetween", notBetween);


        /* ================= TIME ================= */

        operations.put("timeIs", eq);
        operations.put("timeBefore", lt);
        operations.put("timeAfter", gt);
        operations.put("timeBetween", between);
        operations.put("timeNotBetween", notBetween);


        /* ================= DURATION ================= */

        operations.put("durationIs", eq);
        operations.put("durationGreaterThan", gt);
        operations.put("durationGreaterThanEqualTo", gte);
        operations.put("durationLessThan", lt);
        operations.put("durationLessThanEqualTo", lte);
        operations.put("durationBetween", between);
        operations.put("durationNotBetween", notBetween);
    }

    public FilterOperation get(String operator) {
        return operations.getOrDefault(operator, operations.get("none"));
    }


    /* ================= UTIL METHODS ================= */

    private double num(Object v) {
        if (v == null) return 0;
        return ((Number) v).doubleValue();
    }

    private String str(Object v) {
        if (v == null) return "";
        return v.toString().toLowerCase();
    }
}