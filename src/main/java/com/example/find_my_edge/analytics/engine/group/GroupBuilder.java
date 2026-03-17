package com.example.find_my_edge.analytics.engine.group;

import com.example.find_my_edge.analytics.config.GroupConfig;
import com.example.find_my_edge.analytics.engine.group.model.Group;
import com.example.find_my_edge.analytics.engine.group.model.GroupCollection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class GroupBuilder {

    private final GroupCompiler compiler;

    public GroupCollection buildGroups(
            List<String> ids,
            GroupConfig groupSpec,
            BiFunction<String, String, Object> getValue
    ) {

        if (groupSpec == null)
            return new GroupCollection(Collections.emptyList(), Collections.emptyMap());

        Function<String, Object> getKeyFn =
                compiler.compile(groupSpec, getValue);

        Map<String, Group> map = new LinkedHashMap<>();

        for (String id : ids) {

            Object raw = getKeyFn.apply(id);
            String key = GroupKeyUtil.getGroupKey(raw);

            Group group = map.computeIfAbsent(
                    key, k -> Group.builder()
                                   .groupId(key)
                                   .key(key)
                                   .meta(raw)
                                   .ids(new ArrayList<>())
                                   .build()
            );
            group.getIds().add(id);
        }

        List<Group> list =
                map.values().stream()
                   .sorted((a, b) -> {
                       Double aNum = a.getValue();
                       Double bNum = b.getValue();

                       if (aNum != null && bNum != null) {
                           return Double.compare(aNum, bNum);
                       }

                       return a.getKey().compareTo(b.getKey());
                   })
                   .toList();
        List<String> groupId = new ArrayList<>();
        Map<String, Group> groupMap = new HashMap<>();

        list.forEach(g -> {
            groupId.add(g.getGroupId());
            groupMap.put(g.getGroupId(), g);
        });

        return new GroupCollection(groupId, groupMap);
    }

}