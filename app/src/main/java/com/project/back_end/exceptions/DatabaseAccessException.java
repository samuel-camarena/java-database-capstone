package com.project.back_end.exceptions;

import org.springframework.http.HttpStatus;

public class DatabaseAccessException extends BusinessLogicException {
    public DatabaseAccessException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
