package com.project.back_end.exceptions;

import org.springframework.http.HttpStatus;

public class DatabaseAccessException extends BusinessLogicException {
    private static final String DEFAULT_MSG = "Database access error";
    private static final HttpStatus STATUS = HttpStatus.INTERNAL_SERVER_ERROR;
    
    public DatabaseAccessException() {
        super(DEFAULT_MSG, STATUS);
    }
    
    public DatabaseAccessException(String message) {
        super(message == null || message.isBlank() ? DEFAULT_MSG : message , STATUS);
    }
    
    public DatabaseAccessException(Throwable cause) {
        super(DEFAULT_MSG, STATUS, cause);
    }
    
    public DatabaseAccessException(String message, Throwable cause) {
        super(message == null || message.isBlank() ? DEFAULT_MSG : message , STATUS, cause);
    }
}
