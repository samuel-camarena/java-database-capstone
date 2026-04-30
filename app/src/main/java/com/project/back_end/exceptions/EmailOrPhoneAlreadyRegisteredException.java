package com.project.back_end.exceptions;

import org.springframework.http.HttpStatus;

public class EmailOrPhoneAlreadyRegisteredException extends BusinessLogicException {
    private static final String DEFAULT_MSG = "Email or phone already registered";
    private static final HttpStatus STATUS = HttpStatus.CONFLICT;
    
    public EmailOrPhoneAlreadyRegisteredException() {
        super(DEFAULT_MSG, STATUS);
    }
    
    public EmailOrPhoneAlreadyRegisteredException(String message) {
        super(message == null || message.isBlank() ? DEFAULT_MSG : message , STATUS);
    }
    
    public EmailOrPhoneAlreadyRegisteredException(Throwable cause) {
        super(DEFAULT_MSG, STATUS, cause);
    }
    
    public EmailOrPhoneAlreadyRegisteredException(String message, Throwable cause) {
        super(message == null || message.isBlank() ? DEFAULT_MSG : message , STATUS, cause);
    }
}
