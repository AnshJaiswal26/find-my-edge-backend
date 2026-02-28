package com.example.find_my_edge.analytics.ast.parser;

import com.example.find_my_edge.analytics.ast.enums.NodeType;
import com.example.find_my_edge.analytics.ast.exception.AstParseException;
import com.example.find_my_edge.analytics.ast.function.FunctionDefinition;
import com.example.find_my_edge.analytics.ast.function.FunctionRegistry;
import com.example.find_my_edge.analytics.ast.function.enums.FunctionType;
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
                FunctionDefinition fnDef = functionRegistry.get(name);

                if (fnDef == null) {
                    throw error("Unknown function: " + name);
                }

                int actualArgs = t.getArgCount();
                int expectedArgs = fnDef.getMeta().argTypes().length;

                if (actualArgs != expectedArgs) {
                    throw error(
                            "Function " + name + " expects " + expectedArgs +
                            " argument(s) but got " + actualArgs
                    );
                }

                if (stack.size() < actualArgs) {
                    throw error("Function " + name + " expects " + actualArgs + " arguments");
                }

                List<AstNode> args = new ArrayList<>();

                for (int i = 0; i < actualArgs; i++) {
                    args.addFirst(stack.pop()); // reverse order
                }

                // 🚫 Prevent nested window
                if (isWindowFunction(fnDef.getReducer())) {
                    for (AstNode arg : args) {
                        if (containsWindowFunction(arg)) {
                            throw error("Nested window functions not allowed in " + name);
                        }
                    }
                }

                AstNode node = AstNode.builder()
                                      .type(NodeType.FUNCTION)
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
                                      .type(NodeType.IDENTIFIER)
                                      .field(key)
                                      .build();

                stack.push(node);
                continue;
            }

            /* ---------- STRING ---------- */
            if (t.getType() == Tokenizer.Token.Type.STRING) {

                AstNode node = AstNode.builder()
                                      .type(NodeType.CONSTANT)
                                      .valueType("string")
                                      .value(t.getValue()) // string stored separately if needed
                                      .build();

                stack.push(node);
                continue;
            }

            /* ---------- NUMBER ---------- */
            if (t.getType() == Tokenizer.Token.Type.NUMBER) {

                AstNode node = AstNode.builder()
                                      .type(NodeType.CONSTANT)
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
                                          .type(NodeType.UNARY)
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
                                      .type(NodeType.BINARY)
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

        if (node.getType() == NodeType.FUNCTION) {
            FunctionDefinition def = functionRegistry.get(node.getFn());

            if (def != null && isWindowFunction(def.getReducer())) return true;

            for (AstNode arg : node.getArgs()) {
                if (containsWindowFunction(arg)) return true;
            }
        }

        if (node.getType() == NodeType.BINARY) {
            return containsWindowFunction(node.getLeft()) ||
                   containsWindowFunction(node.getRight());
        }

        if (node.getType() == NodeType.UNARY) {
            return containsWindowFunction(node.getArg());
        }

        return false;
    }

    private RuntimeException error(String msg) {
        return new AstParseException("[AST Builder]", msg);
    }
}