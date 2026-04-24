package com.project.back_end.exceptions;

public class InternalServerErrorException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Internal Server error";
    
    public InternalServerErrorException() {
        super(DEFAULT_MESSAGE);
    }
    
    public InternalServerErrorException(String message) {
        super(message != null && message.isBlank() ? message : DEFAULT_MESSAGE);
    }
    
    public InternalServerErrorException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }
    
    public InternalServerErrorException(String message, Throwable cause) {
        super(message != null && message.isBlank() ? message : DEFAULT_MESSAGE, cause);
    }
}
