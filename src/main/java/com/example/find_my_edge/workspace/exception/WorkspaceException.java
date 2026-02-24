package com.example.find_my_edge.workspace.exception;

public class WorkspaceException extends RuntimeException {
    public WorkspaceException(String message) {
        super(message);
    }

    public WorkspaceException(String message, Throwable cause) {
        super(message, cause);
    }
}
