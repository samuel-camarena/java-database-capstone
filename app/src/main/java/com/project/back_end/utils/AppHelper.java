package com.project.back_end.utils;

import com.project.back_end.models.Doctor;
//import com.project.back_end.utils.outputhelpers.MessageFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.project.back_end.utils.outputhelpers.MessageFormatter.MessageHead;
import static org.springframework.http.HttpStatus.CREATED;


public class AppHelper {
    private static final String DOCTOR_SERVICE_MSG = "DoctorService_OLD::";

    private static final Logger logger = LoggerFactory.getLogger(AppHelper.class);
    
    public static <T> ResponseEntity<Map<String, T>> composeResponse(
        OperationStatus status,
        String key,
        T payload) throws IllegalArgumentException {
        
        ResponseEntity.BodyBuilder res;
        switch(status) {
            case SUCCESS -> { res = ResponseEntity.ok(); }
            case CREATED -> { res = ResponseEntity.created((URI) payload); }
            case FAIL -> { res = ResponseEntity.badRequest(); }
            case SERVER_ERR -> { res = ResponseEntity.internalServerError(); }
            case UNAUTHORIZED -> { res = ResponseEntity.status(HttpStatus.UNAUTHORIZED); }
            case NOT_FOUND -> { res = ResponseEntity.status(HttpStatus.NOT_FOUND); }
            default -> { throw new IllegalArgumentException(
                "::composeResponse:: illegal OperationStatus Switch case value"); }
        }
        return res.body(Map.of(key, payload));
    }
    
    public static <T> void logMsg(
        String methodName,
        OperationStatus status,
        String logMsg,
        T data) throws IllegalArgumentException {
        
        String format = "{}:: {}";
        String headerTitle = methodName;
        switch(status) {
            case SUCCESS -> {
                if (data instanceof Integer) { // TODO: Generalizar la especificidad del DOCTOR_SERVICE_MSG -> ¿Transformation o Refactoring?
                    logger.info(format, MessageHead.SUCCESS.compose() +  headerTitle, "Operations status: " + data);
                } else if (data instanceof List<?> list) {
                    logger.info(format, MessageHead.SUCCESS.compose() +  headerTitle, "Size list: " + list.size());
                } else {
                    logger.info(format, MessageHead.SUCCESS.compose() +  headerTitle, "No data available for logging");
                }
            }
            case FAIL -> {
                logger.error(format, MessageHead.FAIL.compose() + headerTitle, logMsg);
            }
            case SERVER_ERR -> {
                logger.error(format, MessageHead.ERROR.compose() + headerTitle, logMsg);
            }
            case UNAUTHORIZED -> {
                logger.error(format, MessageHead.WARNING.compose() + headerTitle, logMsg);
            }
            default -> {
                throw new IllegalArgumentException(DOCTOR_SERVICE_MSG +
                    "::composeDoctorsListResponse:: illegal OperationStatus Switch case value");
            }
        }
        
    }
    
    public static String composeMsg(String reason, String param, String value) {
        return String.format("%s by %s: %s", reason, param, value);
    }

}
