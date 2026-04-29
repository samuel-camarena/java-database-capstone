package com.project.back_end.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class AppHelper {
    
    public static <T> ResponseEntity<Map<String, T>> composeResponse(
        HttpStatus status,
        String key,
        T payload) throws IllegalArgumentException {
        
        return ResponseEntity.status(status).body(Map.of(key, payload));
    }
}
