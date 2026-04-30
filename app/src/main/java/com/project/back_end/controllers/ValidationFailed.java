package com.project.back_end.controllers;

import com.project.back_end.utils.outputhelpers.MessageFormatter.MsgHeader;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

import static com.project.back_end.utils.AppHelper.composeResponse;

@RestControllerAdvice
public class ValidationFailed {
    private static final Logger logger = LoggerFactory.getLogger(ValidationFailed.class);
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        // Iterate through all the validation errors
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            String errorMessage = error.getDefaultMessage();
            errors.put("message", "" + errorMessage);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {
        String msg = ex.getConstraintViolations().iterator().next().getMessage();
        logger.error("{}handleConstraintViolation:: {}", MsgHeader.ERROR.compose(), msg);
        return composeResponse(HttpStatus.BAD_REQUEST, "error", msg);
    }
}