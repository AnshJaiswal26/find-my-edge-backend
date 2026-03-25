package com.example.find_my_edge.analytics.ast.validator;

import com.example.find_my_edge.analytics.ast.enums.ValueType;
import com.example.find_my_edge.analytics.ast.exception.AstTypeValidationException;
import com.example.find_my_edge.analytics.ast.function.FunctionDefinition;
import com.example.find_my_edge.analytics.ast.function.FunctionRegistry;
import com.example.find_my_edge.analytics.ast.function.enums.FunctionMode;
import com.example.find_my_edge.analytics.ast.model.AstNode;
import com.example.find_my_edge.schema.model.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AstTypeValidator {

    private final FunctionRegistry functionRegistry;

    public ValueType validate(AstNode node, FunctionMode mode, Map<String, Schema> schemasById) {
        return resolve(node, mode, schemasById);
    }

    private ValueType resolve(AstNode node, FunctionMode mode, Map<String, Schema> schemasById) {
        if (node == null) return null;

        return switch (node.getType()) {

            case CONSTANT -> inferConstant(node.getValue());

            case IDENTIFIER -> resolveField(node, schemasById);

            case UNARY -> resolve(node.getArg(), mode, schemasById);

            case BINARY -> validateBinary(node, mode, schemasById);

            case FUNCTION -> validateFunction(node, mode, schemasById);

            default -> throw new AstTypeValidationException("Unknown node " + node.getType());
        };
    }

    private ValueType inferConstant(Object value) {
        if (value instanceof Number) return ValueType.NUMBER;
        if (value instanceof String) return ValueType.STRING;
        if (value instanceof Boolean) return ValueType.BOOLEAN;

        throw new AstTypeValidationException("Unsupported constant type");
    }

    private ValueType resolveField(AstNode node, Map<String, Schema> schemasById) {
        Schema schema = schemasById.get(node.getField());

        if (schema == null) {
            throw new AstTypeValidationException("Unknown field: " + node.getField());
        }

        return ValueType.valueOf(schema.getSemanticType().toString());
    }


    private ValueType validateBinary(AstNode node, FunctionMode mode, Map<String, Schema> schemasById) {

        ValueType left = resolve(node.getLeft(), mode, schemasById);
        ValueType right = resolve(node.getRight(), mode, schemasById);
        String op = node.getOp();

        // DATE - DATE → DURATION
        if ("-".equals(op) && left == right && isDateLike(left)) {
            return ValueType.DURATION;
        }

        if (List.of("+", "-", "*", "/").contains(op)) {

            if (left == ValueType.DURATION && right == ValueType.DURATION) {
                if (List.of("+", "-").contains(op)) return ValueType.DURATION;
                if ("/".equals(op)) return ValueType.NUMBER;
                throw error("Invalid: duration " + op + " duration");
            }

            if (left == ValueType.DURATION && right == ValueType.NUMBER) {
                if (List.of("*", "/").contains(op)) return ValueType.DURATION;
                throw error("Invalid: duration " + op + " number");
            }

            if (left == ValueType.NUMBER && right == ValueType.DURATION) {
                if ("*".equals(op)) return ValueType.DURATION;
                throw error("Invalid: number " + op + " duration");
            }

            if (left == ValueType.NUMBER && right == ValueType.NUMBER) {
                return ValueType.NUMBER;
            }

            throw error("Invalid arithmetic: " + left + " " + op + " " + right);
        }

        if (List.of(">", "<", ">=", "<=", "==", "!=").contains(op)) {
            if (left != right) {
                throw error("Comparison mismatch: " + left + " " + op + " " + right);
            }
            return ValueType.BOOLEAN;
        }

        if (List.of("AND", "OR").contains(op)) {
            if (left != ValueType.BOOLEAN || right != ValueType.BOOLEAN) {
                throw error("Logical op requires boolean");
            }
            return ValueType.BOOLEAN;
        }

        throw error("Unknown operator " + op);
    }

    private ValueType validateFunction(AstNode node, FunctionMode mode, Map<String, Schema> schemasById) {

        String fn = node.getFn();
        FunctionDefinition def = functionRegistry.get(fn);

        if (def == null) {
            throw error("Unknown function " + fn);
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
        List<Object> expectedArgs = def.getMeta().args; // from JSON
        Map<String, List<String>> generics = def.getMeta().generics;

        Map<String, ValueType> typeEnv = new HashMap<>();

        List<ValueType> actualTypes = args.stream()
                                          .map(arg -> resolve(arg, mode, schemasById))
                                          .toList();

        for (int i = 0; i < expectedArgs.size(); i++) {
            resolveExpected(expectedArgs.get(i), actualTypes.get(i), typeEnv, generics, fn, i);
        }

        String ret = def.getMeta().returnType;

        if (ret == null) return null;

        if (ret.startsWith("$")) {
            if (!typeEnv.containsKey(ret)) {
                throw error("Unresolved generic " + ret + " in " + fn);
            }
            return typeEnv.get(ret);
        }

        return ValueType.valueOf(ret.toUpperCase());
    }

    private void resolveExpected(
            Object expected,
            ValueType actual,
            Map<String, ValueType> typeEnv,
            Map<String, List<String>> generics,
            String fn,
            int index
    ) {

        // GENERIC ($T)
        if (expected instanceof String str && str.startsWith("$")) {

            List<String> constraint = generics != null ? generics.get(str) : null;

            if (constraint != null &&
                constraint.stream().noneMatch(t -> t.equalsIgnoreCase(actual.name()))) {

                throw error("Function " + fn + " arg " + (index + 1) +
                            ": " + actual + " not allowed for " + str);
            }

            if (!typeEnv.containsKey(str)) {
                typeEnv.put(str, actual);
                return;
            }

            if (typeEnv.get(str) != actual) {
                throw error("Function " + fn + " arg " + (index + 1) +
                            ": expected " + typeEnv.get(str) + " but got " + actual);
            }

            return;
        }

        // UNION
        if (expected instanceof List<?> list) {
            boolean match = list.stream()
                                .anyMatch(t -> t.toString().equalsIgnoreCase(actual.name()));

            if (!match) {
                throw error("Function " + fn + " arg " + (index + 1) +
                            ": expected " + list + " but got " + actual);
            }
            return;
        }

        // EXACT
        if (!expected.toString().equalsIgnoreCase(actual.name())) {
            throw error("Function " + fn + " arg " + (index + 1) +
                        ": expected " + expected + " but got " + actual);
        }
    }

    private boolean isDateLike(ValueType type) {
        return type == ValueType.DATE ||
               type == ValueType.TIME ||
               type == ValueType.DATETIME;
    }

    private AstTypeValidationException error(String msg) {
        return new AstTypeValidationException("[Type Error] " + msg);
    }
}