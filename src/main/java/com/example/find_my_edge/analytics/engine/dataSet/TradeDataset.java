package com.example.find_my_edge.analytics.engine.dataSet;

public interface TradeDataset {

    Object getValue(int index, String schemaKey);

    int size();
}