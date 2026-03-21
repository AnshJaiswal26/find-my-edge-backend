package com.example.find_my_edge.trade_setup.service.impl;

import com.example.find_my_edge.analytics.config.FilterConfig;
import com.example.find_my_edge.analytics.engine.filter.FilterOperation;
import com.example.find_my_edge.analytics.engine.filter.FilterOperationRegistry;
import com.example.find_my_edge.analytics.model.ComputationContext;
import com.example.find_my_edge.trade_setup.enums.Tag;
import com.example.find_my_edge.trade_setup.model.EvaluationResult;
import com.example.find_my_edge.trade_setup.model.FieldMatch;
import com.example.find_my_edge.trade_setup.model.SetupField;
import com.example.find_my_edge.trade_setup.model.TradeSetup;
import com.example.find_my_edge.trade_setup.service.TradeSetupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class SetupScoreComputeService {

    private final TradeSetupService tradeSetupService;

    private final FilterOperationRegistry filterOperationRegistry;

    public Map<String, Map<String, EvaluationResult>> computeAllScores(
            List<TradeSetup> setups,
            ComputationContext ctx
    ) {

        Map<String, Map<String, EvaluationResult>> updates = new HashMap<>();

        for (TradeSetup setup : setups) {
            computeScore(ctx, setup, updates);
        }

        return updates;

    }

    public Map<String, Map<String, EvaluationResult>> computeAffectedScores(
            Set<String> affectedSchemas,
            ComputationContext ctx
    ) {

        List<TradeSetup> setups = tradeSetupService.getAll();

        Map<String, Map<String, EvaluationResult>> updates = new HashMap<>();

        for (TradeSetup setup : setups) {

            if (!isAffected(setup, affectedSchemas)) continue;

            computeScore(ctx, setup, updates);
        }

        return updates;
    }

    private void computeScore(
            ComputationContext ctx,
            TradeSetup setup,
            Map<String, Map<String, EvaluationResult>> updates) {

        for (String tradeId : ctx.getTradeOrder()) {

            Map<String, Object> computed = ctx.getComputed().get(tradeId);
            Map<String, Object> raw = ctx.getRaw().get(tradeId);


            EvaluationResult result = evaluate(setup, raw, computed);

            updates
                    .computeIfAbsent(tradeId, k -> new HashMap<>())
                    .put(setup.getId(), result);
        }
    }

    private EvaluationResult evaluate(
            TradeSetup setup,
            Map<String, Object> raw,
            Map<String, Object> computed
    ) {

        double positiveTotal = 0;
        double positiveAchieved = 0;

        double penalty = 0;

        Map<String, FieldMatch> fieldMatches = new HashMap<>();

        for (SetupField field : setup.getFieldsById().values()) {

            FieldMatch fieldMatch = new FieldMatch();

            Object tradeValue = raw.get(field.getMappedSchemaId());

            if (tradeValue == null) {
                tradeValue = computed.get(field.getMappedSchemaId());
            }

            if (tradeValue == null) {
                fieldMatch.setMatch(false);
                fieldMatches.put(field.getId(), fieldMatch);
                continue;
            }

            fieldMatch.setActualValue(tradeValue);
            fieldMatch.setExpectedValue(field.getExpected());

            boolean match = evaluateCondition(field, tradeValue);

            fieldMatch.setMatch(match);

            Tag tag = field.getTag();

            // ================= POSITIVE SIGNALS =================
            if (isPositive(tag)) {

                double w = positiveWeight(tag);
                positiveTotal += w;

                if (match) {
                    positiveAchieved += w;
                }
            }

            // ================= NEGATIVE SIGNALS =================
            else if (isNegative(tag)) {

                if (match) { // bad condition triggered
                    penalty += negativeWeight(tag);
                }
            }

            fieldMatches.put(field.getId(), fieldMatch);
        }

        // ================= BASE SCORE =================
        double baseScore = positiveTotal == 0
                           ? 0
                           : (positiveAchieved / positiveTotal) * 100;

        double penaltyFactor = positiveTotal == 0 ? 10 : (100.0 / positiveTotal);
        double finalScore = baseScore - (penalty * penaltyFactor);

        return new EvaluationResult(
                round(finalScore),
                tagForScore(finalScore),
                fieldMatches
        );
    }


    private boolean isPositive(Tag tag) {
        return tag == Tag.GOOD ||
               tag == Tag.VERY_GOOD ||
               tag == Tag.EXCELLENT;
    }

    private boolean isNegative(Tag tag) {
        return tag == Tag.BAD ||
               tag == Tag.VERY_BAD ||
               tag == Tag.WORST;
    }

    private double positiveWeight(Tag tag) {
        return switch (tag) {
            case GOOD -> 1.5;
            case VERY_GOOD -> 2.0;
            case EXCELLENT -> 3.0;
            default -> 0.0;
        };
    }

    private double negativeWeight(Tag tag) {
        return switch (tag) {
            case BAD -> 1.0;
            case VERY_BAD -> 2.0;
            case WORST -> 3.0;
            default -> 0.0;
        };
    }

    private Tag tagForScore(double score) {
        if (score >= 90) return Tag.EXCELLENT;
        else if (score >= 80) return Tag.VERY_GOOD;
        else if (score >= 60) return Tag.GOOD;
        else if (score >= 40) return Tag.NEUTRAL;
        else if (score >= 20) return Tag.BAD;
        else if (score >= 10) return Tag.VERY_BAD;
        else return Tag.WORST;
    }

    private boolean evaluateCondition(SetupField field, Object tradeValue) {
        FilterOperation filterOperation = filterOperationRegistry.get(field.getCondition());


        return filterOperation.apply(
                tradeValue, new FilterConfig(
                        field.getExpected(),
                        field.getFrom(),
                        field.getTo()
                )
        );

    }

    private double round(double val) {
        return Math.round(val * 100.0) / 100.0;
    }

    private boolean isAffected(TradeSetup setup, Set<String> affectedSchemas) {
        return setup.getFieldsById()
                    .values()
                    .stream()
                    .anyMatch(f -> affectedSchemas.contains(f.getMappedSchemaId()));
    }
}
