package com.example.find_my_edge.analytics.engine.context;

import com.example.find_my_edge.analytics.ast.context.SchemaType;
import com.example.find_my_edge.analytics.ast.enums.ComputationMode;
import com.example.find_my_edge.analytics.ast.executor.RowSequenceExecutor;
import com.example.find_my_edge.analytics.ast.mapper.AstNodeMapper;
import com.example.find_my_edge.analytics.ast.model.AstNode;
import com.example.find_my_edge.analytics.ast.util.DependencyResolver;
import com.example.find_my_edge.analytics.ast.util.SchemaTypeResolver;
import com.example.find_my_edge.analytics.model.ComputationContext;
import com.example.find_my_edge.schema.enums.ComputeMode;
import com.example.find_my_edge.schema.model.Schema;
import com.example.find_my_edge.schema.model.SchemaBundle;
import com.example.find_my_edge.schema.service.SchemaService;
import com.example.find_my_edge.trade.model.Trade;
import com.example.find_my_edge.trade.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TradeContextBuilder {

    private final DependencyResolver dependencyResolver;

    private final RowSequenceExecutor rowSequenceExecutor;

    private final SchemaService schemaService;
    private final TradeService tradeService;

    public ComputationContext buildContext() {
        SchemaBundle schemaBundle = schemaService.getAll();
        List<Trade> trades = tradeService.getAll();

        Map<String, Schema> schemasById = schemaBundle.getSchemasById();
        List<String> schemasOrder = schemaBundle.getSchemasOrder();

        return buildComputationContext(schemasOrder, schemasById, trades);
    }

    public ComputationContext buildContext(
            Map<String, Schema> schemasById,
            List<String> schemasOrder,
            List<Trade> trades
    ) {
        return buildComputationContext(schemasOrder, schemasById, trades);
    }

    private ComputationContext buildComputationContext(
            List<String> schemasOrder,
            Map<String, Schema> schemasById,
            List<Trade> trades
    ) {

        Map<String, Map<String, Object>> raw = new HashMap<>();
        Map<String, Map<String, Object>> computed = new HashMap<>();
        List<String> tradeOrder = new ArrayList<>();

        if (trades == null || trades.isEmpty()) {
            return new ComputationContext(raw, computed, tradeOrder, schemasById, schemasOrder);
        }

        // STEP 1: RAW
        for (Trade trade : trades) {
            if (trade == null || trade.getId() == null) continue;

            String tradeId = trade.getId();
            tradeOrder.add(tradeId);

            Map<String, Object> rawMap = new HashMap<>();

            // core trade fields
            rawMap.put("id", trade.getId());
            rawMap.put("externalId", trade.getExternalId());
            rawMap.put("date", trade.getDate());
            rawMap.put("entryTime", trade.getEntryTime());
            rawMap.put("exitTime", trade.getExitTime());
            rawMap.put("symbol", trade.getSymbol());
            rawMap.put("direction", trade.getDirection());
            rawMap.put("qty", trade.getQty());
            rawMap.put("entryPrice", trade.getEntryPrice());
            rawMap.put("exitPrice", trade.getExitPrice());
            rawMap.put("charges", trade.getCharges());

            // dynamic schema values
            if (trade.getValues() != null) {
                trade.getValues().forEach((k, v) -> {
                    if (k != null && v != null) {
                        rawMap.put(k, v);
                    }
                });
            }

            raw.put(tradeId, rawMap);
            computed.put(tradeId, new HashMap<>());
        }

        // STEP 2: Dependency resolution
        List<String> executionOrder = dependencyResolver.resolveExecutionOrder(
                dependencyResolver.buildDependencyMap(schemasById.values())
        );

        Map<String, SchemaType> schemaTypeMap = SchemaTypeResolver.buildSchemaTypeMap(schemasById);

        // STEP 3: Row computation
        for (String schemaId : executionOrder) {

            Schema schema = schemasById.get(schemaId);
            if (schema == null || schema.getAst() == null) continue;

            AstNode astNode = AstNodeMapper.toNode(schema.getAst());

            rowSequenceExecutor.execute(
                    astNode,
                    schemaId,
                    0,
                    schema.getInitialValue(),

                    // setter
                    (index, key, val) -> {
                        if (index < 0 || val == null || index >= trades.size()) return;

                        String tradeId = trades.get(index).getId();
                        if (tradeId == null) return;

                        computed.get(tradeId).put(key, val);
                    },

                    // getter
                    (index, key) -> {
                        if (index < 0 || index >= trades.size()) return null;

                        String tradeId = trades.get(index).getId();
                        if (tradeId == null) return null;

                        Map<String, Object> c = computed.get(tradeId);
                        if (c != null && c.containsKey(key)) return c.get(key);

                        Map<String, Object> r = raw.get(tradeId);
                        return r != null ? r.get(key) : null;
                    },

                    trades::size,
                    schemaTypeMap::get,
                    schema.getMode() == ComputeMode.ROW
                    ? ComputationMode.BASE
                    : ComputationMode.WINDOW
            );
        }

        return new ComputationContext(
                Map.copyOf(raw),
                Map.copyOf(computed),
                List.copyOf(tradeOrder),
                schemasById,
                schemasOrder
        );
    }
}
