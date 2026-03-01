package com.example.find_my_edge.analytics.engine.group.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Group {

    public Group(String key, Double value) {
        this.key = key;
        this.value = value;
    }

    private String groupId;
    private String key;
    private Double value;
    private Object meta;
    private List<String> tradeIds;
}
