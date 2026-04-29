package com.project.back_end.controllers;

import com.project.back_end.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(InvalidJwtTokenException.class) // TODO: ¿nombre handle, handelInvalidToken, handleJwtTokenException ??
    public ProblemDetail handleInvalidJwtToken(InvalidJwtTokenException ex) {
        logger.warn("Authentication Error: {}", ex.getMessage());
        return composeProblem(ex.getStatus(), ex.getMessage());
    }
    
    @ExceptionHandler(ParseJwtTokenException.class)
    public ProblemDetail handleParseJwtToken(ParseJwtTokenException ex) {
        logger.warn("Authentication Error: {}", ex.getMessage());
        return composeProblem(ex.getStatus(), ex.getMessage());
    }
    
    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    public ProblemDetail handleEmailAlreadyRegistered(EmailAlreadyRegisteredException ex) {
        logger.warn("Register Error: {}", ex.getMessage());
        return composeProblem(ex.getStatus(), ex.getMessage());
    }
    
    @ExceptionHandler(DatabaseAccessException.class)
    public ProblemDetail handleDatabaseAccess(DatabaseAccessException ex) {
        logger.warn("Persistence Layer Error: {}", ex.getMessage());
        return composeProblem(ex.getStatus(), ex.getMessage());
    }
    
    @ExceptionHandler(CustomCredentialNotFoundException.class)
    public ProblemDetail handleCustomCredentialNotFound(CustomCredentialNotFoundException ex) {
        logger.warn("Security Layer Error: {}", ex.getMessage());
        return composeProblem(ex.getStatus(), ex.getMessage());
    }
    
    private ProblemDetail composeProblem(HttpStatus status, String message) {
        ProblemDetail pd =  ProblemDetail.forStatusAndDetail(status, message);
        pd.setProperties(Map.of("message", message));
        pd.setProperties(Map.of("error", message));
        return pd;
    }
}
