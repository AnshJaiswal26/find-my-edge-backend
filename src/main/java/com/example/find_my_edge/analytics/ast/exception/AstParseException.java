package com.example.find_my_edge.analytics.ast.exception;


public class AstParseException extends AstException {

    private String code;

    public AstParseException(String code, String message) {
        super(message);
        this.code = code;
    }

    public AstParseException(String message, Throwable cause) {
        super(message, cause);
    }
}