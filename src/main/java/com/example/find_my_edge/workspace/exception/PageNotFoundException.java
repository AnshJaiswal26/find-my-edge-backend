package com.example.find_my_edge.workspace.exception;

public class PageNotFoundException extends WorkspaceException {
    public PageNotFoundException(String pageName) {
        super("Page not found: " + pageName);
    }
}
