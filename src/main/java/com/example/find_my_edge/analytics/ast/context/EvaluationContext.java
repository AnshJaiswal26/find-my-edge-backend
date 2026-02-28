package com.example.find_my_edge.analytics.ast.context;

public interface EvaluationContext {

    Object getKeyValue(String key);

    SchemaType getSchemaType(String key);

    Integer getWindowStartIndex();

    void setTradeIndex(int index);

    Integer getTradeCount();

    Integer getTradeIndex();

    Object getTradeValue(int index, String key);

    Object getPrevValue();

}