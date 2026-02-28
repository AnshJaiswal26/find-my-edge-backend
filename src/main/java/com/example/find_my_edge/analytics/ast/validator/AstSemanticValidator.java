package com.example.find_my_edge.analytics.ast.validator;

import com.example.find_my_edge.analytics.ast.enums.ValueType;
import com.example.find_my_edge.analytics.ast.exception.AstTypeValidationException;
import com.example.find_my_edge.analytics.ast.function.FunctionDefinition;
import com.example.find_my_edge.analytics.ast.function.FunctionRegistry;
import com.example.find_my_edge.analytics.ast.model.AstNode;
import com.example.find_my_edge.domain.schema.enums.FieldType;
import com.example.find_my_edge.domain.schema.model.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AstSemanticValidator {

    private final FunctionRegistry functionRegistry;

    public ValueType validate(AstNode node, Map<String, Schema> schemasById) {
        return resolve(node, schemasById);
    }

    private ValueType resolve(AstNode node, Map<String, Schema> schemasById) {

        if (node == null) return ValueType.ANY;

        switch (node.getType()) {

            /* ------------------ CONSTANT ------------------ */
            case CONSTANT -> {
                Object value = node.getValue();

                if (value instanceof Number) return ValueType.NUMBER;
                if (value instanceof String) return ValueType.STRING;

                return ValueType.ANY;
            }

            /* ------------------ IDENTIFIER ------------------ */
            case IDENTIFIER -> {
                Schema schema = schemasById.get(node.getField());
                return schema != null
                       ? mapSemanticType(schema.getType())
                       : ValueType.ANY;
            }

            /* ------------------ UNARY ------------------ */
            case UNARY -> {
                return resolve(node.getArg(), schemasById);
            }

            /* ------------------ BINARY ------------------ */
            case BINARY -> {

                ValueType left = resolve(node.getLeft(), schemasById);
                ValueType right = resolve(node.getRight(), schemasById);
                String op = node.getOp();

                /* ---------- ARITHMETIC ---------- */
                if (List.of("+", "-", "*", "/").contains(op)) {

                    // DATE - DATE → DURATION
                    if ("-".equals(op) && left == right && isDateLike(left)) {
                        return ValueType.DURATION;
                    }

                    // duration rules
                    if (left == ValueType.DURATION && right == ValueType.DURATION) {
                        if (List.of("+", "-").contains(op)) return ValueType.DURATION;
                        if ("/".equals(op)) return ValueType.NUMBER;
                        throw error("Invalid operation: duration " + op + " duration");
                    }

                    if (left == ValueType.DURATION && right == ValueType.NUMBER) {
                        if (List.of("*", "/").contains(op)) return ValueType.DURATION;
                        throw error("Invalid operation: duration " + op + " number");
                    }

                    if (left == ValueType.NUMBER && right == ValueType.DURATION) {
                        if ("*".equals(op)) return ValueType.DURATION;
                        throw error("Invalid operation: number " + op + " duration");
                    }

                    // number rules
                    if (left == ValueType.NUMBER && right == ValueType.NUMBER) {
                        return ValueType.NUMBER;
                    }

                    throw error("Invalid arithmetic: " + left + " " + op + " " + right);
                }

                /* ---------- COMPARISON ---------- */
                if (List.of(">", "<", ">=", "<=", "==", "!=").contains(op)) {
                    if (left != right) {
                        throw error("Invalid comparison: " + left + " " + op + " " + right);
                    }
                    return ValueType.BOOLEAN;
                }

                /* ---------- LOGICAL ---------- */
                if (List.of("AND", "OR").contains(op)) {
                    if (left != ValueType.BOOLEAN || right != ValueType.BOOLEAN) {
                        throw error("Invalid logical op: " + left + " " + op + " " + right);
                    }
                    return ValueType.BOOLEAN;
                }

                return ValueType.ANY;
            }

            /* ------------------ FUNCTION ------------------ */
            case FUNCTION -> {

                String fn = node.getFn();
                FunctionDefinition def = functionRegistry.get(fn);

                if (def == null) {
                    throw error("Unknown function " + fn);
                }

                List<AstNode> args = node.getArgs();
                Object[] expectedArgs = def.getMeta().argTypes();

                for (int i = 0; i < expectedArgs.length; i++) {

                    Object expected = expectedArgs[i];
                    ValueType actual = resolve(args.get(i), schemasById);

                    validateArgumentType(fn, i, expected, actual);
                }

                /* ---------- RETURN TYPE ---------- */

                String returnType = def.getMeta().returnType();

                if ("same".equalsIgnoreCase(returnType)) {
                    return resolve(args.getFirst(), schemasById);
                }

                return returnType != null
                       ? ValueType.valueOf(returnType.toUpperCase())
                       : ValueType.ANY;
            }
        }

        return ValueType.ANY;
    }

    /* ---------- Helpers ---------- */

    private boolean isDateLike(ValueType type) {
        return type == ValueType.DATE ||
               type == ValueType.TIME ||
               type == ValueType.DATETIME;
    }

    private void validateArgumentType(String fn, int index, Object expected, ValueType actual) {

        if ("any".equalsIgnoreCase(expected.toString())) return;

        // union types
        if (expected instanceof List<?> list) {
            boolean match = list.stream()
                                .anyMatch(t -> t.toString().equalsIgnoreCase(actual.name()));

            if (!match) {
                throw error("Function " + fn + " argument " + (index + 1) +
                            " must be one of " + list + ", got " + actual);
            }
            return;
        }

        // direct match
        if (!expected.toString().equalsIgnoreCase(actual.name())) {
            throw error("Function " + fn + " argument " + (index + 1) +
                        " must be " + expected + ", got " + actual);
        }
    }

    private ValueType mapSemanticType(FieldType type) {
        return switch (type) {
            case FieldType.NUMBER -> ValueType.NUMBER;
            case FieldType.TEXT, FieldType.SELECT -> ValueType.STRING;
            case FieldType.BOOLEAN -> ValueType.BOOLEAN;
            case FieldType.DATE -> ValueType.DATE;
            case FieldType.TIME -> ValueType.TIME;
            case FieldType.DATETIME -> ValueType.DATETIME;
            case FieldType.DURATION -> ValueType.DURATION;
            default -> ValueType.ANY;
        };
    }

    private AstTypeValidationException error(String msg) {
        return new AstTypeValidationException("[Semantic Error] " + msg);
    }
}