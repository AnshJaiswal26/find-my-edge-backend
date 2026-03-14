package com.example.find_my_edge.analytics.compute;

import com.example.find_my_edge.analytics.config.GroupConfig;
import com.example.find_my_edge.analytics.engine.group.GroupBuilder;
import com.example.find_my_edge.analytics.engine.group.model.Group;
import com.example.find_my_edge.analytics.model.ComputationContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupComputeService {

    private final GroupBuilder groupBuilder;

    public List<Group> buildGroups(
            ComputationContext ctx,
            GroupConfig config,
            boolean includeTradeIds
    ) {

        return groupBuilder.buildGroups(
                ctx.getTradeOrder(),
                config,
                (tradeId, key) -> {

                    Object value = ctx.getRaw().get(tradeId).get(key);

                    if (value == null) {
                        value = ctx.getComputed().get(tradeId).get(key);
                    }

                    return value;
                },
                includeTradeIds
        );
    }
}