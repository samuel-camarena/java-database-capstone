package com.project.back_end.exceptions;

import org.springframework.http.HttpStatus;

public class ResourceUpdateFailedException extends BusinessLogicException {
    private static final String DEFAULT_MSG = "Resource creation failed";
    private static final HttpStatus STATUS = HttpStatus.CONFLICT;
    
    public ResourceUpdateFailedException() {
        super(DEFAULT_MSG, STATUS);
    }
    
    public ResourceUpdateFailedException(String message) {
        super(message != null && message.isBlank() ? message : DEFAULT_MSG, STATUS);
    }
    
    public ResourceUpdateFailedException(Throwable cause) {
        super(DEFAULT_MSG, STATUS, cause);
    }
    
    public ResourceUpdateFailedException(String message, Throwable cause) {
        super(message != null && message.isBlank() ? message : DEFAULT_MSG, STATUS, cause);
    }
}
