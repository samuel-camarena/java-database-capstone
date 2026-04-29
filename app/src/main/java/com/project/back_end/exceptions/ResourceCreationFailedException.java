package com.project.back_end.exceptions;

import org.springframework.http.HttpStatus;

public class ResourceCreationFailedException extends BusinessLogicException {
    private static final String DEFAULT_MSG = "Resource creation failed";
    private static final HttpStatus STATUS = HttpStatus.CONFLICT;
    
    public ResourceCreationFailedException() {
        super(DEFAULT_MSG, STATUS);
    }
    
    public ResourceCreationFailedException(String message) {
        super(message != null && message.isBlank() ? message : DEFAULT_MSG, STATUS);
    }
    
    public ResourceCreationFailedException(Throwable cause) {
        super(DEFAULT_MSG, STATUS, cause);
    }
    
    public ResourceCreationFailedException(String message, Throwable cause) {
        super(message != null && message.isBlank() ? message : DEFAULT_MSG, STATUS, cause);
    }
}
