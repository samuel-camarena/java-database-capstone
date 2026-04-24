package com.project.back_end.controllers;


import com.project.back_end.models.Doctor;
import com.project.back_end.models.Prescription;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.MainService;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.utils.outputhelpers.MessageFormatter;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.project.back_end.utils.AppHelper.composeResponse;
import static com.project.back_end.utils.OperationStatus.*;

/**
 * This controller manages creating and retrieving prescriptions tied to appointments.
 */
@RestController
@RequestMapping("${api.path}" + "v1/prescription")
public class PrescriptionController {
    private static final Logger logger = LoggerFactory.getLogger(PrescriptionController.class);
    @Autowired
    private final PrescriptionService prescriptionService;
    @Autowired
    private final MainService mainService;
    @Autowired
    private final AppointmentService appointmentService; // AppointmentService` to updateDoctor appointment status after a prescription is issued.
    
    public PrescriptionController(PrescriptionService prescriptionService, MainService mainService, AppointmentService appointmentService) {
        this.prescriptionService = prescriptionService;
        this.mainService = mainService;
        this.appointmentService = appointmentService;
    }
    
    /**
     * Handles HTTP POST requests to save a new prescription for a given appointment.
     * - Accepts a validated `Prescription` object in the request body and a doctor’s token as a path variable.
     * - If the token is valid, updates the status of the corresponding appointment to reflect that a prescription has been added.
     * - Delegates the saving logic to `PrescriptionService` and returns a response indicating success or failure.
     *
     * @param user
     * @param token
     * @param prescription
     * @return
     * @throws Exception
     */
    @PostMapping("/save")
    public ResponseEntity<Map<String, Integer>> savePrescription(
        @RequestHeader("X-User") @Valid String user,
        @RequestHeader("Authorization") @Valid String token,
        @RequestBody @Valid Prescription prescription) throws Exception {
        
        // Validates the token for the `"doctor"` role.
        if (mainService.validateToken(token, user).getStatusCode().isSameCodeAs(HttpStatus.UNAUTHORIZED)) {
            logger.error("{}saveDoctor", MessageFormatter.MessageHead.UNAUTHORIZED.compose());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        appointmentService.updateStatus(prescription.getAppointmentId(), 1);
        logger.info("{}savePrescription", MessageFormatter.MessageHead.SUCCESS.compose());
        return prescriptionService.savePrescription(prescription);
    }
    
    /**
     * Handles HTTP GET requests to retrieve a prescription by its associated appointment ID.
     * - Accepts the appointment ID and a doctor’s token as path variables.
     * - Validates the token for the `"doctor"` role using the shared service.
     * - If the token is valid, fetches the prescription using the `PrescriptionService`.
     * - Returns the prescription details or an appropriate error message if validation fails.
     *
     * @param user
     * @param token
     * @param appointmentId
     * @return
     * @throws Exception
     */
    @PostMapping("/{appointmentId}")
    public ResponseEntity<Map<String, List<Prescription>>> getPrescription(
        @RequestHeader("X-User") @Valid String user,
        @RequestHeader("Authorization") @Valid String token,
        @PathVariable @Valid long appointmentId) throws Exception {
        
        // Validates the token for the `"doctor"` role.
        if (mainService.validateToken(token, user).getStatusCode().isSameCodeAs(HttpStatus.UNAUTHORIZED)) {
            logger.error("{}getPrescription", MessageFormatter.MessageHead.UNAUTHORIZED.compose());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        if (!prescriptionService.getPrescription(appointmentId).hasBody()) {
            logger.error("{}getPrescription", MessageFormatter.MessageHead.FAIL.compose());
            return composeResponse(FAIL, "prescriptions", List.of());
        }
        
        List<Prescription> prescriptions = prescriptionService.getPrescription(appointmentId).getBody().get("prescriptions");
        if (prescriptions.isEmpty()) {
            logger.error("{}getPrescription", MessageFormatter.MessageHead.FAIL.compose());
            return composeResponse(FAIL, "prescriptions", List.of());
        }
        
        logger.info("{}getPrescription", MessageFormatter.MessageHead.SUCCESS.compose());
        return composeResponse(SUCCESS, "prescriptions", prescriptions);
    }
}
