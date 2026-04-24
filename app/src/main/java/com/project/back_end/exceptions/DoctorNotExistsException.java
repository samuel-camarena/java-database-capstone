package com.project.back_end.exceptions;

public class DoctorNotExistsException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Doctor does not exist";
    
    public DoctorNotExistsException() {
        super(DEFAULT_MESSAGE);
    }
    
    public DoctorNotExistsException(String message) {
        super(message != null && message.isBlank() ? message : DEFAULT_MESSAGE);
    }
    
    public DoctorNotExistsException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }
    
    public DoctorNotExistsException(String message, Throwable cause) {
        super(message != null && message.isBlank() ? message : DEFAULT_MESSAGE, cause);
    }
}
