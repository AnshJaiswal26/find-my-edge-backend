package com.example.find_my_edge.trade.mapper;

import com.example.find_my_edge.trade.entity.TradeEntity;
import com.example.find_my_edge.trade.model.Trade;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TradeEntityMapper {

    /* ---------------- MODEL → ENTITY ---------------- */

    public TradeEntity toEntity(Trade model) {
        if (model == null) return null;

        TradeEntity entity = new TradeEntity();

        entity.setId(model.getId()); // for update

        Map<String, Object> values =
                model.getValues() != null
                ? new HashMap<>(model.getValues())
                : new HashMap<>();

        // 🔥 Extract structured fields from values

        entity.setExternalId((String) values.get("externalId"));
        values.remove("externalId");

        entity.setDate(getLong(values.get("date")));
        values.remove("date");

        entity.setEntryTime(getLong(values.get("entryTime")));
        values.remove("entryTime");

        entity.setExitTime(getLong(values.get("exitTime")));
        values.remove("exitTime");

        entity.setSymbol((String) values.get("symbol"));
        values.remove("symbol");

        entity.setDirection((String) values.get("direction"));
        values.remove("direction");

        entity.setPnl(getDouble(values.get("pnl")));
        values.remove("pnl");

        entity.setCharges(getDouble(values.get("charges")));
        values.remove("charges");

        entity.setQuantity(getInteger(values.get("qty")));
        values.remove("qty");

        // 🔥 Keep full values JSON (including structured fields if you want)
        entity.setValues(values);

        return entity;
    }

    /* ---------------- ENTITY → MODEL ---------------- */

    public Trade toDomain(TradeEntity entity) {
        if (entity == null) return null;

        Map<String, Object> values =
                entity.getValues() != null
                ? new HashMap<>(entity.getValues())
                : new HashMap<>();

        // 🔥 Ensure structured fields are always present in values

        putIfNotNull(values, "externalId", entity.getExternalId());

        putIfNotNull(values, "date", entity.getDate());
        putIfNotNull(values, "entryTime", entity.getEntryTime());
        putIfNotNull(values, "exitTime", entity.getExitTime());

        putIfNotNull(values, "symbol", entity.getSymbol());
        putIfNotNull(values, "direction", entity.getDirection());

        putIfNotNull(values, "pnl", entity.getPnl());
        putIfNotNull(values, "charges", entity.getCharges());
        putIfNotNull(values, "quantity", entity.getQuantity());

        return Trade.builder()
                    .id(entity.getId())
                    .values(values)
                    .build();
    }

    /* ---------------- HELPERS ---------------- */

    private Long getLong(Object val) {
        if(val == null) return null;

        return val instanceof Number num ? ((Number) num).longValue() : 0;
    }

    private Double getDouble(Object val) {
        if(val == null) return null;

        return val instanceof Number num ? ((Number) num).doubleValue() : 0.0;
    }

    private Integer getInteger(Object val) {
        if(val == null) return null;

        return val instanceof Number num ? ((Number) num).intValue() : 0;
    }

    private void putIfNotNull(Map<String, Object> map, String key, Object value) {
        if (value != null) {
            map.put(key, value);
        }
    }
}