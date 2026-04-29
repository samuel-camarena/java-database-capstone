package com.project.back_end.exceptions;

import org.springframework.http.HttpStatus;

public abstract class BusinessLogicException extends RuntimeException {
    private final HttpStatus status;
    
    public BusinessLogicException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
    
    public BusinessLogicException(String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }
    
    public HttpStatus getStatus() {
        return status;
    }
}
