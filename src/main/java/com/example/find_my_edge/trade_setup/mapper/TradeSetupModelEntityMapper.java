package com.example.find_my_edge.trade_setup.mapper;

import com.example.find_my_edge.common.util.JsonUtil;
import com.example.find_my_edge.trade_setup.entity.SetupFieldEntity;
import com.example.find_my_edge.trade_setup.entity.TradeSetupEntity;
import com.example.find_my_edge.trade_setup.model.SetupField;
import com.example.find_my_edge.trade_setup.model.TradeSetup;
import lombok.RequiredArgsConstructor;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
@RequiredArgsConstructor
public abstract class TradeSetupModelEntityMapper {

    protected JsonUtil jsonUtil;

    @Autowired
    public void setJsonUtil(JsonUtil jsonUtil) {
        this.jsonUtil = jsonUtil;
    }

    /* ================= MODEL → ENTITY ================= */

    @Mapping(target = "trades", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "fieldOrder", expression = "java(jsonUtil.toJsonList(model.getFieldOrder()))")
    @Mapping(target = "fields", expression = "java(mapFields(model.getFieldsById(), entity))")
    public abstract TradeSetupEntity toEntity(TradeSetup model, @Context TradeSetupEntity entity);

    /* ================= ENTITY → MODEL ================= */

    @Mapping(target = "fieldOrder", expression = "java(jsonUtil.fromJsonList(entity.getFieldOrder(), String.class))")
    @Mapping(target = "fieldsById", expression = "java(mapFieldsToMap(entity.getFields()))")
    public abstract TradeSetup toModel(TradeSetupEntity entity);

    /* ================= FIELD ================= */

    @Mapping(target = "tradeSetup", ignore = true)
    @Mapping(target = "expected", expression = "java(jsonUtil.toJson(model.getExpected()))")
    public abstract SetupFieldEntity toEntity(SetupField model);

    @Mapping(target = "expected", expression = "java(jsonUtil.fromJson(entity.getExpected(), Object.class))")
    public abstract SetupField toModel(SetupFieldEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "tradeSetup", ignore = true)
    @Mapping(target = "expected", expression = "java(model.getExpected() != null ? jsonUtil.toJson(model.getExpected()) : entity.getExpected())")
    public abstract void updateEntityFromModel(
            SetupField model,
            @MappingTarget SetupFieldEntity entity
    );

    /* ================= CUSTOM ================= */

    protected List<SetupFieldEntity> mapFields(
            Map<String, SetupField> map,
            @Context TradeSetupEntity parent
    ) {
        if (map == null) return new ArrayList<>();

        List<SetupFieldEntity> list = new ArrayList<>();

        for (Map.Entry<String, SetupField> entry : map.entrySet()) {
            SetupFieldEntity field = toEntity(entry.getValue());

            field.setTradeSetup(parent); // critical

            list.add(field);
        }

        return list;
    }

    protected Map<String, SetupField> mapFieldsToMap(List<SetupFieldEntity> list) {
        if (list == null) return new HashMap<>();

        Map<String, SetupField> map = new HashMap<>();

        for (SetupFieldEntity entity : list) {
            map.put(entity.getId(), toModel(entity));
        }

        return map;
    }
}