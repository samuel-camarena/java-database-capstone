package com.project.back_end.exceptions;

import org.springframework.http.HttpStatus;

public class CustomCredentialNotFoundException extends BusinessLogicException {
    private static final String DEFAULT_MSG = "Credential not found";
    private static final HttpStatus STATUS = HttpStatus.UNAUTHORIZED;
    
    public CustomCredentialNotFoundException() {
        super(DEFAULT_MSG, STATUS);
    }
    
    public CustomCredentialNotFoundException(String message) {
        super(message != null && message.isBlank() ? message : DEFAULT_MSG, STATUS);
    }
    
    public CustomCredentialNotFoundException(Throwable cause) {
        super(DEFAULT_MSG, STATUS, cause);
    }
    
    public CustomCredentialNotFoundException(String message, Throwable cause) {
        super(message != null && message.isBlank() ? message : DEFAULT_MSG, STATUS, cause);
    }
}
