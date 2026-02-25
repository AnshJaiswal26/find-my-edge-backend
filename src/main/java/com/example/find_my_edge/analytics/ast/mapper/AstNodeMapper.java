package com.example.find_my_edge.analytics.ast.mapper;

import com.example.find_my_edge.analytics.ast.model.AstNode;
import com.example.find_my_edge.common.config.AstConfig;

import java.util.List;
import java.util.stream.Collectors;

public class AstNodeMapper {

    public static AstNode toNode(AstConfig config) {
        if (config == null) return null;

        return AstNode.builder()
                      .type(AstNode.NodeType.valueOf(config.getType().toUpperCase()))
                      .op(config.getOp())

                      // recursive mapping
                      .left(toNode(config.getLeft()))
                      .right(toNode(config.getRight()))

                      .key(config.getKey())

                      .value(config.getValue())
                      .valueType(config.getValueType())

                      .fn(config.getFn())

                      .arg(toNode(config.getArg()))

                      .args(mapArgs(config.getArgs()))
                      .build();
    }

    private static List<AstNode> mapArgs(List<AstConfig> args) {
        if (args == null) return null;

        return args.stream()
                   .map(AstNodeMapper::toNode)
                   .collect(Collectors.toList());
    }
}