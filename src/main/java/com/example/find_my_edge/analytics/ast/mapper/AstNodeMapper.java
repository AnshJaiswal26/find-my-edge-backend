package com.example.find_my_edge.analytics.ast.mapper;

import com.example.find_my_edge.analytics.ast.enums.NodeType;
import com.example.find_my_edge.analytics.ast.exception.AstParseException;
import com.example.find_my_edge.analytics.ast.model.AstNode;
import com.example.find_my_edge.common.config.AstConfig;

import java.util.List;
import java.util.stream.Collectors;

public class AstNodeMapper {

    public static AstNode toNode(AstConfig config) {
        if (config == null) return null;

        NodeType type;
        try {
            type = config.getType();
        } catch (Exception e) {
            throw new AstParseException(
                    "[AstNode Type Error]",
                    "Invalid node type: " + config.getType()
            );
        }

        return AstNode.builder()
                      .type(type)
                      .op(config.getOp())

                      // recursive mapping
                      .left(toNode(config.getLeft()))
                      .right(toNode(config.getRight()))

                      .field(config.getField())

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