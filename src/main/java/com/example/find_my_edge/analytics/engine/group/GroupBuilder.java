package com.example.find_my_edge.analytics.engine.group;

import com.example.find_my_edge.analytics.config.GroupConfig;
import com.example.find_my_edge.analytics.engine.group.model.Group;
import com.example.find_my_edge.domain.trade.model.Trade;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@Component
public class GroupBuilder {
    public List<Group> buildGroups(
            List<String> tradeOrder,
            Map<String, Trade> tradesById,
            GroupConfig groupSpec,
            BiFunction<Trade, String, Object> getValue,
            GroupCompiler compiler
    ) {

        if (groupSpec == null) return null;

        Function<Trade, Object> getKeyFn =
                compiler.compile(groupSpec, getValue);

        Map<String, Group> map = new LinkedHashMap<>();

        for (String tradeId : tradeOrder) {
            Trade trade = tradesById.get(tradeId);

            Object raw = getKeyFn.apply(trade);
            String key = GroupKeyUtil.getGroupKey(raw);

            map.computeIfAbsent(
                       key, k -> Group.builder()
                                      .groupId(key)
                                      .key(key)
                                      .tradeIds(new ArrayList<>())
                                      .build()
               )
               .getTradeIds()
               .add(tradeId);
        }

        return map.values().stream()
                  .sorted((a, b) -> {
                      Double aNum = a.getValue();
                      Double bNum = b.getValue();

                      if (aNum != null && bNum != null) {
                          return Double.compare(aNum, bNum);
                      }

                      return a.getKey().compareTo(b.getKey());
                  })
                  .toList();
    }

}