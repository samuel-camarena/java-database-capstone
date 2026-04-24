package com.project.back_end.exceptions;

public class DoctorRepositoryException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Doctor Repository generic error";
    
    public DoctorRepositoryException() {
        super(DEFAULT_MESSAGE);
    }
    
    public DoctorRepositoryException(String message) {
        super(message != null && message.isBlank() ? message : DEFAULT_MESSAGE);
    }
    
    public DoctorRepositoryException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }
    
    public DoctorRepositoryException(String message, Throwable cause) {
        super(message != null && message.isBlank() ? message : DEFAULT_MESSAGE, cause);
    }
}
