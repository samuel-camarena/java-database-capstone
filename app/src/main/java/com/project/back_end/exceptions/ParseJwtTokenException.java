package com.project.back_end.exceptions;

import org.springframework.http.HttpStatus;

public class ParseJwtTokenException extends BusinessLogicException {
    private static final String DEFAULT_MSG = "Error while parsing JWT Token to extract a subject";
    private static final HttpStatus STATUS = HttpStatus.INTERNAL_SERVER_ERROR;
    
    public ParseJwtTokenException() {
        super(DEFAULT_MSG, STATUS);
    }
    
    public ParseJwtTokenException(String message) {
        super(message != null && message.isBlank() ? message : DEFAULT_MSG, STATUS);
    }
    
    public ParseJwtTokenException(Throwable cause) {
        super(DEFAULT_MSG, STATUS, cause);
    }
    
    public ParseJwtTokenException(String message, Throwable cause) {
        super(message != null && message.isBlank() ? message : DEFAULT_MSG, STATUS, cause);
    }
}
