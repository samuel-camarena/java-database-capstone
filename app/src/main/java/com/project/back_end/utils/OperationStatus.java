package com.project.back_end.utils;

public enum OperationStatus {
    SUCCESS (1),
    CREATED (201),
    FAIL (-1),
    SERVER_ERR (0),
    UNAUTHORIZED (401),
    NOT_FOUND (28);
    
    private final int status;
    
    OperationStatus(int status) {
        this.status = status;
    }
    
    public int getStatus() {
        return this.status;
    }
}