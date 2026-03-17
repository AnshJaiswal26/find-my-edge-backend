package com.example.find_my_edge.analytics.engine.group.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class GroupCollection {

    private List<String> groupIds;
    private Map<String, Group> groupsById;
}
