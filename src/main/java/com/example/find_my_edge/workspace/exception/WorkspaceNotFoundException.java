package com.example.find_my_edge.workspace.exception;

import java.util.UUID;

public class WorkspaceNotFoundException extends WorkspaceException {
    public WorkspaceNotFoundException(UUID userId) {
        super("Workspace not found for user: " + userId.toString());
    }

    public WorkspaceNotFoundException() {
        super("Workspace has no pages");
    }
}
