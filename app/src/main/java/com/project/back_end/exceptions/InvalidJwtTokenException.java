package com.project.back_end.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidJwtTokenException extends BusinessLogicException {
    private static final String DEFAULT_MSG = "JWT Token invalid";
    private static final HttpStatus STATUS = HttpStatus.CONFLICT;
    
    public InvalidJwtTokenException() {
        super(DEFAULT_MSG, STATUS);
    }
    
    public InvalidJwtTokenException(String message) {
        super(message != null && message.isBlank() ? message : DEFAULT_MSG, STATUS);
    }
    
    public InvalidJwtTokenException(Throwable cause) {
        super(DEFAULT_MSG, STATUS, cause);
    }
    
    public InvalidJwtTokenException(String message, Throwable cause) {
        super(message != null && message.isBlank() ? message : DEFAULT_MSG, STATUS, cause);
    }
}
