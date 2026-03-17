package com.example.find_my_edge.analytics.compute;

import com.example.find_my_edge.analytics.config.GroupConfig;
import com.example.find_my_edge.analytics.engine.group.GroupBuilder;
import com.example.find_my_edge.analytics.engine.group.model.GroupCollection;
import com.example.find_my_edge.analytics.model.ComputationContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class GroupComputeService {

    private final GroupBuilder groupBuilder;

    public GroupCollection buildGroups(
            ComputationContext ctx,
            GroupConfig config
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
                }
        );
    }
}