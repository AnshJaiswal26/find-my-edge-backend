package com.example.find_my_edge.analytics.ast.parser;

import com.example.find_my_edge.analytics.ast.exception.AstParseException;
import com.example.find_my_edge.analytics.ast.function.FunctionRegistry;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class Tokenizer {

    private final FunctionRegistry functionRegistry;

    public Tokenizer(FunctionRegistry functionRegistry) {
        this.functionRegistry = functionRegistry;
    }

    private static final String OPS = "+-*/()";
    private static final List<String> COMPARATORS = List.of("<=", ">=", "==", "!=", "<", ">");
    private static final Set<String> LOGICAL_OPS = Set.of("AND", "OR");

    public List<Token> tokenize(String expr) {

        List<Token> tokens = new ArrayList<>();
        StringBuilder buf = new StringBuilder();

        int i = 0;
        Token prevToken = null;
        int parenBalance = 0;

        while (i < expr.length()) {
            char ch = expr.charAt(i);

            /* ---------- whitespace ---------- */
            if (Character.isWhitespace(ch)) {
                prevToken = flushIdentifier(buf, tokens, prevToken, expr, i);
                i++;
                continue;
            }

            /* ---------- [identifier] ---------- */
            if (ch == '[') {
                prevToken = flushIdentifier(buf, tokens, prevToken, expr, i);
                i++;

                StringBuilder name = new StringBuilder();
                while (i < expr.length() && expr.charAt(i) != ']') {
                    name.append(expr.charAt(i++));
                }

                if (i >= expr.length()) throw error("Unclosed '['");

                i++; // skip ]

                Token t = new Token(Token.Type.IDENTIFIER, name.toString().trim());
                tokens.add(t);
                prevToken = t;
                continue;
            }

            /* ---------- @{id} ---------- */
            if (ch == '@' && i + 1 < expr.length() && expr.charAt(i + 1) == '{') {
                prevToken = flushIdentifier(buf, tokens, prevToken, expr, i);
                i += 2;

                StringBuilder id = new StringBuilder();
                while (i < expr.length() && expr.charAt(i) != '}') {
                    id.append(expr.charAt(i++));
                }

                if (i >= expr.length()) throw error("Unclosed '@{'");

                i++;

                Token t = new Token(Token.Type.IDENTIFIER, id.toString().trim(), true);
                tokens.add(t);
                prevToken = t;
                continue;
            }

            /* ---------- string ---------- */
            if (ch == '"') {
                prevToken = flushIdentifier(buf, tokens, prevToken, expr, i);
                i++;

                StringBuilder str = new StringBuilder();
                while (i < expr.length() && expr.charAt(i) != '"') {
                    str.append(expr.charAt(i++));
                }

                if (i >= expr.length()) throw error("Unclosed string literal");

                i++;

                Token t = new Token(Token.Type.STRING, str.toString());
                tokens.add(t);
                prevToken = t;
                continue;
            }

            /* ---------- comparators ---------- */
            boolean matched = false;
            for (String op : COMPARATORS) {
                if (expr.startsWith(op, i)) {
                    prevToken = flushIdentifier(buf, tokens, prevToken, expr, i);

                    Token t = new Token(Token.Type.OP, op);
                    tokens.add(t);
                    prevToken = t;

                    i += op.length();
                    matched = true;
                    break;
                }
            }
            if (matched) continue;

            /* ---------- identifier ---------- */
            if (Character.isLetter(ch) || ch == '_') {
                buf.append(ch);
                i++;

                while (i < expr.length() &&
                       (Character.isLetterOrDigit(expr.charAt(i)) || expr.charAt(i) == '_')) {
                    buf.append(expr.charAt(i++));
                }
                continue;
            }

            prevToken = flushIdentifier(buf, tokens, prevToken, expr, i);

            /* ---------- unary minus ---------- */
            if (ch == '-' && (prevToken == null ||
                              prevToken.getType() == Token.Type.OP ||
                              prevToken.getType() == Token.Type.LPAREN ||
                              prevToken.getType() == Token.Type.FUNCTION)) {

                Token t = new Token(Token.Type.OP, "u-");
                tokens.add(t);
                prevToken = t;
                i++;
                continue;
            }

            /* ---------- number ---------- */
            if (Character.isDigit(ch) || ch == '.') {
                StringBuilder num = new StringBuilder();
                int dotCount = 0;

                while (i < expr.length() &&
                       (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) {

                    if (expr.charAt(i) == '.' && ++dotCount > 1) {
                        throw error("Invalid number format");
                    }

                    num.append(expr.charAt(i++));
                }

                if (!num.toString().matches("\\d+(\\.\\d+)?")) {
                    throw error("Invalid number: " + num);
                }

                Token t = new Token(Token.Type.NUMBER, num.toString());
                tokens.add(t);
                prevToken = t;
                continue;
            }

            /* ---------- comma ---------- */
            if (ch == ',') {
                Token prev = tokens.isEmpty() ? null : tokens.get(tokens.size() - 1);
                char next = (i + 1 < expr.length()) ? expr.charAt(i + 1) : '\0';

                if (prev == null || prev.getType() == Token.Type.COMMA || prev.getType() == Token.Type.LPAREN) {
                    throw error("Unexpected comma");
                }

                if (next == ')') {
                    throw error("Comma before closing parenthesis not allowed");
                }

                Token t = new Token(Token.Type.COMMA, ",");
                tokens.add(t);
                prevToken = null;
                i++;
                continue;
            }

            /* ---------- operators ---------- */
            if (OPS.indexOf(ch) >= 0) {
                Token t;

                if (ch == '(') {
                    t = new Token(Token.Type.LPAREN, "(");
                    parenBalance++;
                } else if (ch == ')') {
                    parenBalance--;
                    if (parenBalance < 0) throw error("Unmatched ')'");
                    t = new Token(Token.Type.RPAREN, ")");
                } else {
                    t = new Token(Token.Type.OP, String.valueOf(ch));
                }

                tokens.add(t);
                prevToken = t;
                i++;
                continue;
            }

            throw error("Invalid character: " + ch);
        }

        flushIdentifier(buf, tokens, prevToken, expr, i);

        if (parenBalance != 0) {
            throw error("Unmatched parentheses");
        }

        return tokens;
    }

    private Token flushIdentifier(StringBuilder buf,
            List<Token> tokens,
            Token prevToken,
            String expr,
            int i) {

        if (buf.length() == 0) return prevToken;

        String value = buf.toString();
        String upper = value.toUpperCase();

        if (LOGICAL_OPS.contains(upper)) {
            Token t = new Token(Token.Type.OP, upper);
            tokens.add(t);
            buf.setLength(0);
            return t;
        }

        boolean isFunction =
                functionRegistry.get(upper) != null &&
                i < expr.length() &&
                expr.charAt(i) == '(';

        Token t = new Token(
                isFunction ? Token.Type.FUNCTION : Token.Type.IDENTIFIER,
                value
        );

        tokens.add(t);
        buf.setLength(0);
        return t;
    }

    private RuntimeException error(String msg) {
        return new AstParseException("[Tokenizer Error]", msg);
    }

    @Getter
    public static class Token {

        public enum Type {
            NUMBER,
            IDENTIFIER,
            FUNCTION,
            OP,
            LPAREN,
            RPAREN,
            COMMA,
            STRING
        }

        private final Type type;
        private final String value;
        private final boolean isId;
        private final int argCount; // 👈 add this

        public Token(Type type, String value) {
            this(type, value, false, -1);
        }

        public Token(Type type, String value, boolean isId) {
            this(type, value, isId, -1);
        }

        public Token(Type type, String value, boolean isId, int argCount) {
            this.type = type;
            this.value = value;
            this.isId = isId;
            this.argCount = argCount;
        }

        public Token withArgCount(int argCount) {
            return new Token(this.type, this.value, this.isId, argCount);
        }
    }
}