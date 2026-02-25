package com.example.find_my_edge.analytics.ast.parser;

import com.example.find_my_edge.analytics.ast.exception.AstException;
import com.example.find_my_edge.analytics.ast.function.FunctionRegistry;
import com.example.find_my_edge.analytics.ast.function.FunctionType;
import com.example.find_my_edge.analytics.ast.model.AstNode;
import com.example.find_my_edge.analytics.ast.model.AstResult;
import com.example.find_my_edge.analytics.ast.reducer.Reducer;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AstBuilder {

    private final FunctionRegistry functionRegistry;

    public AstBuilder(FunctionRegistry functionRegistry) {
        this.functionRegistry = functionRegistry;
    }

    public AstResult build(List<Tokenizer.Token> postfix) {

        Deque<AstNode> stack = new ArrayDeque<>();
        Set<String> dependencies = new HashSet<>();

        for (Tokenizer.Token t : postfix) {

            /* ---------- FUNCTION ---------- */
            if (t.getType() == Tokenizer.Token.Type.FUNCTION) {

                String name = t.getValue().toUpperCase();
                Reducer fnDef = functionRegistry.get(name);

                if (fnDef == null) {
                    throw error("Unknown function: " + name);
                }

                int arity = t.getArgCount();

                if (arity < 0) {
                    throw error("Invalid function arity: " + name);
                }

                if (stack.size() < arity) {
                    throw error("Function " + name + " expects " + arity + " arguments");
                }

                List<AstNode> args = new ArrayList<>();

                for (int i = 0; i < arity; i++) {
                    args.addFirst(stack.pop()); // reverse order
                }

                // 🚫 Prevent nested window
                if (isWindowFunction(fnDef)) {
                    for (AstNode arg : args) {
                        if (containsWindowFunction(arg)) {
                            throw error("Nested window functions not allowed in " + name);
                        }
                    }
                }

                AstNode node = AstNode.builder()
                                      .type(AstNode.NodeType.FUNCTION)
                                      .fn(name)
                                      .args(args)
                                      .build();

                stack.push(node);
                continue;
            }

            /* ---------- IDENTIFIER ---------- */
            if (t.getType() == Tokenizer.Token.Type.IDENTIFIER) {

                String key = t.getValue();
                if (key == null || key.isBlank()) {
                    throw error("Invalid identifier");
                }

                dependencies.add(key);

                AstNode node = AstNode.builder()
                                      .type(AstNode.NodeType.KEY)
                                      .key(key)
                                      .build();

                stack.push(node);
                continue;
            }

            /* ---------- STRING ---------- */
            if (t.getType() == Tokenizer.Token.Type.STRING) {

                AstNode node = AstNode.builder()
                                      .type(AstNode.NodeType.CONSTANT)
                                      .valueType("string")
                                      .value(null) // string stored separately if needed
                                      .key(t.getValue()) // optional: or add separate string field later
                                      .build();

                stack.push(node);
                continue;
            }

            /* ---------- NUMBER ---------- */
            if (t.getType() == Tokenizer.Token.Type.NUMBER) {

                AstNode node = AstNode.builder()
                                      .type(AstNode.NodeType.CONSTANT)
                                      .valueType("number")
                                      .value(Double.valueOf(t.getValue()))
                                      .build();

                stack.push(node);
                continue;
            }

            /* ---------- OPERATOR ---------- */
            if (t.getType() == Tokenizer.Token.Type.OP) {

                String op = t.getValue();

                // unary
                if ("u-".equals(op)) {
                    if (stack.isEmpty()) {
                        throw error("Unary operator missing operand");
                    }

                    AstNode arg = stack.pop();

                    AstNode node = AstNode.builder()
                                          .type(AstNode.NodeType.UNARY)
                                          .op("-")
                                          .arg(arg)
                                          .build();

                    stack.push(node);
                    continue;
                }

                // binary
                if (stack.size() < 2) {
                    throw error("Binary operator missing operands");
                }

                AstNode right = stack.pop();
                AstNode left = stack.pop();

                AstNode node = AstNode.builder()
                                      .type(AstNode.NodeType.BINARY)
                                      .op(op)
                                      .left(left)
                                      .right(right)
                                      .build();

                stack.push(node);
            }
        }

        if (stack.size() != 1) {
            throw error("Invalid expression");
        }

        return new AstResult(stack.pop(), dependencies);
    }

    /* ---------- Helpers ---------- */

    private boolean isWindowFunction(Reducer reducer) {
        return FunctionType.WINDOW.equals(reducer.getType()); // or enum later
    }

    private boolean containsWindowFunction(AstNode node) {

        if (node == null) return false;

        if (node.getType() == AstNode.NodeType.FUNCTION) {
            Reducer def = functionRegistry.get(node.getFn());

            if (def != null && isWindowFunction(def)) return true;

            for (AstNode arg : node.getArgs()) {
                if (containsWindowFunction(arg)) return true;
            }
        }

        if (node.getType() == AstNode.NodeType.BINARY) {
            return containsWindowFunction(node.getLeft()) ||
                   containsWindowFunction(node.getRight());
        }

        if (node.getType() == AstNode.NodeType.UNARY) {
            return containsWindowFunction(node.getArg());
        }

        return false;
    }

    private RuntimeException error(String msg) {
        return new AstException("[AST Builder] " + msg);
    }
}