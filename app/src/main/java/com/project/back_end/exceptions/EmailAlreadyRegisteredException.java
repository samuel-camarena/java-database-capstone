package com.project.back_end.exceptions;

import org.springframework.http.HttpStatus;

public class EmailAlreadyRegisteredException extends BusinessLogicException {
    private static final String DEFAULT_MSG = "Email already registered";
    private static final HttpStatus STATUS = HttpStatus.CONFLICT;
    
    public EmailAlreadyRegisteredException() {
        super(DEFAULT_MSG, STATUS);
    }
    
    public EmailAlreadyRegisteredException(String message) {
        super(message != null && message.isBlank() ? message : DEFAULT_MSG, STATUS);
    }
    
    public EmailAlreadyRegisteredException(Throwable cause) {
        super(DEFAULT_MSG, STATUS, cause);
    }
    
    public EmailAlreadyRegisteredException(String message, Throwable cause) {
        super(message != null && message.isBlank() ? message : DEFAULT_MSG, STATUS, cause);
    }
}
