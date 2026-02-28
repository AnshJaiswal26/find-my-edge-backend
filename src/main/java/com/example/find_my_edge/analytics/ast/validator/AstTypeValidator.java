package com.example.find_my_edge.analytics.ast.validator;

import com.example.find_my_edge.analytics.ast.enums.NodeType;
import com.example.find_my_edge.analytics.ast.enums.ValueType;
import com.example.find_my_edge.analytics.ast.exception.AstTypeValidationException;
import com.example.find_my_edge.analytics.ast.function.FunctionRegistry;
import com.example.find_my_edge.analytics.ast.function.enums.FunctionMode;
import com.example.find_my_edge.analytics.ast.model.AstNode;
import com.example.find_my_edge.domain.schema.enums.FieldType;
import com.example.find_my_edge.domain.schema.model.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AstTypeValidator {

    private final FunctionRegistry functionRegistry;

    public void validate(AstNode ast, FunctionMode mode, Map<String, Schema> schemasById) {
        getNodeType(ast, mode, schemasById);
    }

    private ValueType getNodeType(AstNode node, FunctionMode mode, Map<String, Schema> schemasById) {
        if (node == null) return ValueType.ANY;

        switch (node.getType()) {

            case CONSTANT -> {
                Object value = node.getValue();

                if (value instanceof Number) return ValueType.NUMBER;
                if (value instanceof String) return ValueType.STRING;

                return ValueType.ANY;
            }

            case IDENTIFIER -> {
                if (schemasById == null) return ValueType.ANY;

                Schema schema = schemasById.get(node.getField());

                if (schema == null) {
                    throw new AstTypeValidationException("Unknown field reference: " + node.getField());
                }

                return mapSchemaType(schema.getType());
            }

            case UNARY -> {
                ValueType t = getNodeType(node.getArg(), mode, schemasById);

                if ("-".equals(node.getOp()) && t != ValueType.NUMBER) {
                    throw new AstTypeValidationException("Unary minus requires a number");
                }

                return ValueType.NUMBER;
            }

            case BINARY -> {
                ValueType left = getNodeType(node.getLeft(), mode, schemasById);
                ValueType right = getNodeType(node.getRight(), mode, schemasById);

                String op = node.getOp();

                if (List.of("+", "-", "*", "/").contains(op)) {
                    if (left != ValueType.NUMBER || right != ValueType.NUMBER) {
                        throw new AstTypeValidationException("Operator " + op + " requires numeric operands");
                    }
                    return ValueType.NUMBER;
                }

                if (List.of("==", "!=", ">", "<", ">=", "<=").contains(op)) {
                    return ValueType.BOOLEAN;
                }

                if (List.of("AND", "OR").contains(op)) {
                    if (left != ValueType.BOOLEAN || right != ValueType.BOOLEAN) {
                        throw new AstTypeValidationException(op + " requires boolean operands");
                    }
                    return ValueType.BOOLEAN;
                }
            }

            case FUNCTION -> {
                String fn = node.getFn();

                var def = functionRegistry.get(fn);
                if (def == null) {
                    throw new AstTypeValidationException("Unknown function " + fn);
                }

                // Mode validation
                if (mode != null) {
                    Set<String> allowed = functionRegistry.getAllowedFunctions(mode);

                    if (allowed != null && !allowed.contains(fn)) {
                        throw new AstTypeValidationException(
                                "Function " + fn + " is not allowed in " + mode + " computation"
                        );
                    }
                }

                List<AstNode> args = node.getArgs();
                List<String> argTypes = Arrays.stream(def.getMeta().argTypes()).toList(); // adapt based on your model

                for (int i = 0; i < argTypes.size(); i++) {
                    String expected = argTypes.get(i);

                    ValueType actual = getNodeType(args.get(i), mode, schemasById);

                    validateArgumentType(fn, i, expected, actual, args.get(i), mode, schemasById);
                }

                return def.getMeta().returnType() != null
                       ? ValueType.valueOf(def.getMeta().returnType().toUpperCase())
                       : ValueType.ANY;
            }
        }

        return ValueType.ANY;
    }

    private void validateArgumentType(
            String fn,
            int index,
            Object expected,
            ValueType actual,
            AstNode argNode,
            FunctionMode mode,
            Map<String, Schema> schemasById
    ) {

        // ANY
        if ("any".equals(expected)) return;

        // key constraint (like { key: "number" })
        if (expected instanceof Map<?, ?> map && map.containsKey("key")) {

            if (argNode.getType() != NodeType.IDENTIFIER) {
                throw new AstTypeValidationException(
                        "Function " + fn + " argument " + (index + 1) + " must be a field reference"
                );
            }

            String expectedKeyType = map.get("key").toString();

            ValueType keyType = getNodeType(argNode, mode, schemasById);

            if (!"any".equals(expectedKeyType) &&
                keyType != ValueType.valueOf(expectedKeyType.toUpperCase())) {

                throw new AstTypeValidationException(
                        "Function " + fn + " argument " + (index + 1) +
                        " must reference a " + expectedKeyType + " field"
                );
            }

            return;
        }

        // Union types
        if (expected instanceof List<?> list) {
            boolean match = list.stream()
                                .anyMatch(t -> t.toString().equalsIgnoreCase(actual.name()));

            if (!match) {
                throw new AstTypeValidationException(
                        "Function " + fn + " argument " + (index + 1) +
                        " must be one of " + list
                );
            }
            return;
        }

        System.out.println(actual.name());
        // Direct match
        if (!expected.toString().equalsIgnoreCase(actual.name())) {
            throw new AstTypeValidationException(
                    "Function " + fn + " argument " + (index + 1) +
                    " must be " + expected
            );
        }
    }

    private ValueType mapSchemaType(FieldType type) {
        return switch (type) {
            case FieldType.NUMBER,
                 FieldType.DURATION,
                 FieldType.TIME,
                 FieldType.DATE,
                 FieldType.DATETIME -> ValueType.NUMBER;

            case FieldType.SELECT, FieldType.TEXT -> ValueType.STRING;

            case FieldType.BOOLEAN -> ValueType.BOOLEAN;
            default -> ValueType.ANY;
        };
    }
}