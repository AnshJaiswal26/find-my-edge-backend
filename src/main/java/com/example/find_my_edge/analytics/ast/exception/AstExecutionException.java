package com.example.find_my_edge.analytics.ast.exception;

public class AstExecutionException extends AstException {
    private String code;

    public AstExecutionException(String message) {
        super(message);
    }

    public AstExecutionException(String code, String message) {
        super(message);
        this.code = code;
    }
}
