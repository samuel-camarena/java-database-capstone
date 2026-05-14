package com.project.back_end.controllers;

import com.project.back_end.exceptions.*;
import com.project.back_end.utils.outputhelpers.MessageFormatter.MsgHeader;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;

import static com.project.back_end.utils.AppHelper.composeResponse;

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
        logger.warn("[Business Logic::Register:] {} :: {}", ex.getClass(), ex.getMessage());
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
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleValidationException(MethodArgumentNotValidException ex) {
        ErrorDTO errorDto = new ErrorDTO();
        errorDto.getMetadata()
            .put("numFieldErrors", String.valueOf(ex.getFieldErrorCount()));
        ex.getFieldErrors()
            .forEach(errorDto::addFieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {
        
        
        ErrorDTO errorDto = new ErrorDTO();
        errorDto.metadata.put("numErrors", "2");
        
        
        ex.getConstraintViolations()
            .stream()
            .map(cv -> {
                cv.getPropertyPath().toString();
                cv.getConstraintDescriptor().getAnnotation().annotationType();
                return cv;
            })
            .forEach(System.out::println);
        
        
        String msg = ex.getConstraintViolations().iterator().next().getMessage();
        logger.error("{}handleConstraintViolation:: {}", MsgHeader.ERROR.compose(), msg);
        return composeResponse(HttpStatus.BAD_REQUEST, "error", msg);
    }
    
    private ProblemDetail composeProblem(HttpStatus status, String message) {
        ProblemDetail pd =  ProblemDetail.forStatusAndDetail(status, message);
        pd.setProperties(Map.of("message", message));
        pd.setProperties(Map.of("error", message));
        return pd;
    }
    
    public class ErrorDTO {
        private final Map<String, String> metadata;
        private final Map<String, String> data;
        private final Map<String, List<String>> fieldErrors;
        
        ErrorDTO() {
            data = new HashMap<String, String>();
            metadata = new HashMap<String, String>();
            fieldErrors = new HashMap<String, List<String>>();
        }
        
        public Map<String, String> getMetadata() {
            return metadata;
        }
        
        public Map<String, String> getData() {
            return data;
        }
        
        public Map<String, List<String>> getFieldErrors() {
            return fieldErrors;
        }
        
        public void addFieldErrors(FieldError field) {
            if (field == null || field.getField().isBlank() || field.getDefaultMessage() == null) {
                return;
            }
            String fieldName = field.getField();
            if (fieldErrors.containsKey(field.getField())) {
                fieldErrors
                    .get(field.getField())
                    .add(field.getDefaultMessage());
            } else {
                List<String> fieldMessages = new ArrayList<>();
                fieldMessages.add(field.getDefaultMessage());
                fieldErrors.put(fieldName, fieldMessages);
            }
        }
        
        
//        private class FieldErrorDTO {
            //private final Map<String, List<String>> fieldErrors2;
//            private String fieldName;
//            private List<String> errors;
//
//            public FieldErrorDTO(String fieldName) {
//                this.fieldName = fieldName;
//            }
//
//            public FieldErrorDTO(String fieldName, List<String> errors) {
//                this.fieldName = fieldName;
//                this.errors = errors;
//            }
//
//            public String getFieldName() {
//                return fieldName;
//            }
//
//            public List<String> getErrors() {
//                return errors;
//            }
//
//            public void setFieldName(String fieldName) {
//                this.fieldName = fieldName;
//            }
//
//            public void setErrors(List<String> errors) {
//                this.errors = errors;
//            }
//        }
    }
}
