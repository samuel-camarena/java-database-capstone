package com.project.back_end.exceptions;

import org.springframework.http.HttpStatus;

public class CredentialMismatchedException extends BusinessLogicException {
    private static final String DEFAULT_MSG = "Credential mismatched";
    private static final HttpStatus STATUS = HttpStatus.UNAUTHORIZED;
    
    public CredentialMismatchedException() {
        super(DEFAULT_MSG, STATUS);
    }
    
    public CredentialMismatchedException(String message) {
        super(message == null || message.isBlank() ? DEFAULT_MSG : message , STATUS);
    }
    
    public CredentialMismatchedException(Throwable cause) {
        super(DEFAULT_MSG, STATUS, cause);
    }
    
    public CredentialMismatchedException(String message, Throwable cause) {
        super(message == null || message.isBlank() ? DEFAULT_MSG : message , STATUS, cause);
    }
    
}
