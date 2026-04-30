package com.project.back_end.controllers;

import com.project.back_end.exceptions.*;
import com.project.back_end.utils.outputhelpers.MessageFormatter.MsgHeader;
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
    
    @ExceptionHandler(InvalidJwtTokenException.class)
    public ProblemDetail handleInvalidJwtToken(InvalidJwtTokenException ex) {
        logger.error("{}Security:: Authentication Error: {}", MsgHeader.ERROR.compose(), ex.getMessage());
        String clientMsg = "Invalid authentication by JWT token: user not found by this token";
        return composeProblem(ex.getStatus(), clientMsg);
    }
    
    @ExceptionHandler(ParseJwtTokenException.class)
    public ProblemDetail handleParseJwtToken(ParseJwtTokenException ex) {
        logger.warn("{}Security:: Authentication Error: {}", MsgHeader.ERROR.compose(), ex.getMessage());
        return composeProblem(ex.getStatus(), ex.getMessage());
    }
    
    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    public ProblemDetail handleEmailAlreadyRegistered(EmailAlreadyRegisteredException ex) {
        logger.warn("{}Business Logic:: Register Error: {}", MsgHeader.ERROR.compose(), ex.getMessage());
        return composeProblem(ex.getStatus(), ex.getMessage());
    }
    
    @ExceptionHandler(DatabaseAccessException.class)
    public ProblemDetail handleDatabaseAccess(DatabaseAccessException ex) {
        logger.warn("{}Persistence:: Infrastructure Error: {}", MsgHeader.ERROR.compose(), ex.getMessage());
        return composeProblem(ex.getStatus(), ex.getMessage());
    }
    
    @ExceptionHandler(CustomCredentialNotFoundException.class)
    public ProblemDetail handleCustomCredentialNotFound(CustomCredentialNotFoundException ex) {
        logger.warn("{}Security:: Credentials Error: {}", MsgHeader.ERROR.compose(), ex.getMessage());
        return composeProblem(ex.getStatus(), ex.getMessage());
    }
    
    private ProblemDetail composeProblem(HttpStatus status, String message) {
        ProblemDetail pd =  ProblemDetail.forStatusAndDetail(status, message);
        pd.setProperties(Map.of("message", message));
        pd.setProperties(Map.of("error", message));
        return pd;
    }
}
