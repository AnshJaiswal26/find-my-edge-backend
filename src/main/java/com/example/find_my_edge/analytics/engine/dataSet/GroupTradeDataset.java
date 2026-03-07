package com.example.find_my_edge.analytics.engine.dataSet;

import com.example.find_my_edge.analytics.model.ComputationContext;

import java.util.List;
import java.util.Map;

public class GroupTradeDataset implements TradeDataset {

    private final ComputationContext ctx;
    private final List<String> groupTradeIds;

    public GroupTradeDataset(
            ComputationContext ctx,
            List<String> groupTradeIds
    ) {
        this.ctx = ctx;
        this.groupTradeIds = groupTradeIds;
    }

    @Override
    public Object getValue(int index, String key) {
        if (index < 0 || index >= groupTradeIds.size()) return null;

        String tradeId = groupTradeIds.get(index);

        Map<String, Object> computed = ctx.getComputed().get(tradeId);
        if (computed != null && computed.containsKey(key)) {
            return computed.get(key);
        }

        Map<String, Object> raw = ctx.getRaw().get(tradeId);
        return raw != null ? raw.get(key) : null;
    }

    @Override
    public int size() {
        return groupTradeIds.size();
    }
}