package com.project.back_end.controllers;

import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.mainService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.project.back_end.utils.outputhelpers.MessageFormatter.MessageHead;

@RestController
@RequestMapping("${api.path}" + "v1/doctor")
public class DoctorController {
    
    private static final Logger logger = LoggerFactory.getLogger(DoctorController.class);
    @Autowired
    private final DoctorService doctorService;
    @Autowired
    private final mainService mainService;
    
    public DoctorController(DoctorService doctorService, mainService mainService) {
        this.doctorService = doctorService;
        this.mainService = mainService;
    }
    
    @GetMapping("/availability")
    public ResponseEntity<Map<String, List<String>>> getDoctorAvailability(
        @RequestHeader("X-User") @Valid String user,
        @RequestHeader("Authorization") @Valid String token,
        @RequestParam @Valid long doctorId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Valid LocalDate date) throws Exception {
        
        if (mainService.validateToken(token, user).getStatusCode().isSameCodeAs(HttpStatus.UNAUTHORIZED)) {
            logger.error("{}getDoctorAvailability", MessageHead.UNAUTHORIZED.compose());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        logger.info("{}getDoctorAvailability", MessageHead.SUCCESS.compose());
        return doctorService.getDoctorAvailability(doctorId, date);
    }
    
    
    @GetMapping("/")
    public ResponseEntity<Map<String, List<Doctor>>> getDoctors(
        @RequestHeader("X-User") @Valid String user,
        @RequestHeader("Authorization") @Valid String token) throws Exception {
        
        if (mainService.validateToken(token, user).getStatusCode().isSameCodeAs(HttpStatus.UNAUTHORIZED)) {
            logger.error("{}getDoctors", MessageHead.UNAUTHORIZED.compose());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        logger.info("{}getDoctors", MessageHead.SUCCESS.compose());
        return doctorService.getDoctors();
    }
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, Integer>> saveDoctor(
        @RequestHeader("X-User") @Valid String user,
        @RequestHeader("Authorization") @Valid String token,
        @RequestBody @Valid Doctor doctor) throws Exception {
        
        if (mainService.validateToken(token, user).getStatusCode().isSameCodeAs(HttpStatus.UNAUTHORIZED)) {
            logger.error("{}saveDoctor", MessageHead.UNAUTHORIZED.compose());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        logger.info("{}saveDoctor", MessageHead.SUCCESS.compose());
        return doctorService.registerDoctor(doctor);
    }
    
    @PutMapping("/update")
    public ResponseEntity<Map<String, Integer>> updateDoctor(
        @RequestHeader("X-User") @Valid String user,
        @RequestHeader("Authorization") @Valid String token,
        @RequestBody @Valid Doctor doctor) throws Exception {
        
        if (mainService.validateToken(token, user).getStatusCode().isSameCodeAs(HttpStatus.UNAUTHORIZED)) {
            logger.error("{}updateDoctor", MessageHead.UNAUTHORIZED.compose());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        logger.info("{}updateDoctor", MessageHead.SUCCESS.compose());
        return doctorService.updateDoctor(doctor);
    }
    
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Integer>> deleteDoctor(
        @RequestHeader("X-User") @Valid String user,
        @RequestHeader("Authorization") @Valid String token,
        @PathVariable @Valid long doctorId) throws Exception {
        
        if (mainService.validateToken(token, user).getStatusCode().isSameCodeAs(HttpStatus.UNAUTHORIZED)) {
            logger.error("{}deleteDoctor", MessageHead.UNAUTHORIZED.compose());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        logger.info("{}deleteDoctor", MessageHead.SUCCESS.compose());
        return doctorService.deleteDoctor(doctorId);
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolation(ConstraintViolationException ex) {
        String errorMessage = ex.getConstraintViolations().iterator().next().getMessage();
        logger.error("{}handleConstraintViolation::{}", MessageHead.ERROR.compose(), errorMessage);
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }
}
