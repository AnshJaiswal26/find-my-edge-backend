package com.example.find_my_edge.analytics.ast.parser;

import com.example.find_my_edge.analytics.ast.exception.AstParseException;
import com.example.find_my_edge.analytics.ast.parser.Tokenizer.Token;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PostfixConverter {

    private static final Map<String, Integer> PRECEDENCE = Map.ofEntries(
            Map.entry("u-", 4),
            Map.entry("*", 3),
            Map.entry("/", 3),
            Map.entry("+", 2),
            Map.entry("-", 2),
            Map.entry(">", 1),
            Map.entry("<", 1),
            Map.entry(">=", 1),
            Map.entry("<=", 1),
            Map.entry("==", 1),
            Map.entry("!=", 1),
            Map.entry("AND", 0),
            Map.entry("OR", -1)
    );

    public List<Token> toPostfix(List<Token> tokens) {

        List<Token> out = new ArrayList<>();
        Deque<Token> ops = new ArrayDeque<>();
        Deque<FnContext> fnStack = new ArrayDeque<>();

        Token prevToken = null;

        for (Token t : tokens) {

            FnContext ctx = fnStack.peek();

            /* ---------- Missing operator check ---------- */
            if (ctx != null && !ctx.expectingArg && prevToken != null) {

                boolean prevEndsExpr = isValue(prevToken) || prevToken.getType() == Token.Type.RPAREN;
                boolean currStartsExpr = isValueStart(t);

                if (prevEndsExpr && currStartsExpr) {
                    throw error("Missing operator or comma between arguments");
                }
            }

            /* ---------- VALUE START ---------- */
            if (isValueStart(t)) {
                if (ctx != null && ctx.expectingArg) {
                    ctx.expectingArg = false;
                    ctx.argCount++;
                }
            }

            /* ---------- VALUES ---------- */
            if (isValue(t)) {
                out.add(t);
                prevToken = t;
                continue;
            }

            /* ---------- FUNCTION ---------- */
            if (t.getType() == Token.Type.FUNCTION) {
                ops.push(t);
                prevToken = t;
                continue;
            }

            /* ---------- LEFT PAREN ---------- */
            if (t.getType() == Token.Type.LPAREN) {

                if (!ops.isEmpty() && ops.peek().getType() == Token.Type.FUNCTION) {
                    fnStack.push(new FnContext());
                } else if (ctx != null) {
                    ctx.parenDepth++;
                }

                ops.push(t);
                prevToken = t;
                continue;
            }

            /* ---------- COMMA ---------- */
            if (t.getType() == Token.Type.COMMA) {

                if (ctx == null) throw error("Comma outside function");
                if (ctx.expectingArg) throw error("Unexpected comma");

                ctx.expectingArg = true;

                while (!ops.isEmpty() && ops.peek().getType() != Token.Type.LPAREN) {
                    out.add(ops.pop());
                }

                if (ops.isEmpty()) throw error("Misplaced comma");

                prevToken = t;
                continue;
            }

            /* ---------- OPERATOR ---------- */
            if (t.getType() == Token.Type.OP) {

                Integer prec = PRECEDENCE.get(t.getValue());
                if (prec == null) throw error("Unknown operator: " + t.getValue());

                if (ctx != null && ctx.expectingArg && !"u-".equals(t.getValue())) {
                    throw error("Argument expected before operator");
                }

                while (!ops.isEmpty()
                       && ops.peek().getType() == Token.Type.OP
                       && PRECEDENCE.get(ops.peek().getValue()) >= prec) {

                    out.add(ops.pop());
                }

                ops.push(t);
                prevToken = t;
                continue;
            }

            /* ---------- RIGHT PAREN ---------- */
            if (t.getType() == Token.Type.RPAREN) {

                while (!ops.isEmpty() && ops.peek().getType() != Token.Type.LPAREN) {
                    out.add(ops.pop());
                }

                if (ops.isEmpty()) throw error("Mismatched parentheses");

                ops.pop(); // remove '('

                FnContext fnCtx = fnStack.peek();
                if (fnCtx != null) {

                    if (fnCtx.parenDepth > 0) {
                        fnCtx.parenDepth--;
                    } else {
                        if (fnCtx.expectingArg && fnCtx.argCount > 0) {
                            throw error("Trailing comma in function arguments");
                        }

                        Token fn = ops.pop(); // function
                        Token fnWithArgs = fn.withArgCount(fnCtx.argCount);
                        out.add(fnWithArgs);

                        fnStack.pop();
                    }
                }

                prevToken = t;
            }
        }

        /* ---------- Drain stack ---------- */
        while (!ops.isEmpty()) {
            Token op = ops.pop();
            if (op.getType() == Token.Type.LPAREN || op.getType() == Token.Type.RPAREN) {
                throw error("Mismatched parentheses");
            }
            out.add(op);
        }

        return out;
    }

    /* ---------- Helpers ---------- */

    private boolean isValue(Token t) {
        return t.getType() == Token.Type.IDENTIFIER ||
               t.getType() == Token.Type.NUMBER ||
               t.getType() == Token.Type.STRING;
    }

    private boolean isValueStart(Token t) {
        return isValue(t) ||
               t.getType() == Token.Type.FUNCTION ||
               t.getType() == Token.Type.LPAREN;
    }

    private RuntimeException error(String msg) {
        return new AstParseException("[Parser Error]",  msg);
    }

    /* ---------- Function Context ---------- */
    private static class FnContext {
        int argCount = 0;
        boolean expectingArg = true;
        int parenDepth = 0;
    }
}