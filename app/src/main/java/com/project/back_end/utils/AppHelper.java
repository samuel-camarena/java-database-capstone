package com.project.back_end.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class AppHelper {
    private static final Logger logger = LoggerFactory.getLogger(AppHelper.class);
    
    public static <T> ResponseEntity<Map<String, T>> composeResponse(
        HttpStatus status,
        String key,
        T payload) throws IllegalArgumentException {
        
        logger.debug("[composeResponse::] Status: {}, payload: {}", status, payload);
        //logger.info("[composeResponse::] Status: {}, payload: {}", status, payload);
        return ResponseEntity.status(status).body(Map.of(key, payload));
    }
}
