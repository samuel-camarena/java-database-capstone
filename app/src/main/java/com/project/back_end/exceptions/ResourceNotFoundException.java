package com.project.back_end.exceptions;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BusinessLogicException {
    private static final String DEFAULT_MSG = "Resource not found";
    private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;
    
    public ResourceNotFoundException() {
        super(DEFAULT_MSG, STATUS);
    }
    
    public ResourceNotFoundException(String message) {
        super(message == null || message.isBlank() ? DEFAULT_MSG : message , STATUS);
    }
    
    public ResourceNotFoundException(Throwable cause) {
        super(DEFAULT_MSG, STATUS, cause);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message == null || message.isBlank() ? DEFAULT_MSG : message , STATUS, cause);
    }
}
