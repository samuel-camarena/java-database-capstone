package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.MainService;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.utils.outputhelpers.MessageFormatter.MessageHead;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.project.back_end.services.AppointmentService.Status.COMPLETED;
import static com.project.back_end.utils.AppHelper.composeResponse;

/**
 * This controller manages creating and retrieving prescriptions tied to appointments.
 */
@RestController
@RequestMapping("${api.path}" + "v1/prescription")
public class PrescriptionController {
    private static final Logger logger = LoggerFactory.getLogger(PrescriptionController.class);
    private final PrescriptionService prescriptionService;
    private final MainService mainService;
    private final AppointmentService appointmentService;
    
    @Autowired
    public PrescriptionController(PrescriptionService prescriptionService, MainService mainService, AppointmentService appointmentService) {
        this.prescriptionService = prescriptionService;
        this.mainService = mainService;
        this.appointmentService = appointmentService;
    }
    
    /**
     * Handles HTTP POST requests to save a new prescription for a given appointment.<p>
     * * Accepts a validated `Prescription` object in the request body and a doctor’s token as a path variable.<br>
     * * If the token is valid, updates the status of the corresponding appointment to reflect that a prescription has been added.</p>
     * @param token JWT token of a doctor
     * @param prescription valid prescription object
     * @return Delegates the saving logic to `PrescriptionService` and returns a response indicating success or failure.
     */
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> savePrescription(
        @PathVariable("Authorization") @Valid String token,
        @RequestBody @Valid Prescription prescription) {
        
        mainService.validateToken(token, "doctor");
        prescriptionService.savePrescription(prescription);
        appointmentService.updateStatus(prescription.getAppointmentId(), COMPLETED.getValue());
        logger.info("{}savePrescription:: {}", MessageHead.SUCCESS.compose(), "Prescription successfully saved");
        return composeResponse(HttpStatus.CREATED, "message", "Prescription successfully saved");
    }
    
    /**
     * Handles HTTP GET requests  by its associated appointment ID.<p>
     * * Accepts the appointment ID and a doctor’s token as path variables.<br>
     * * Validates the token for the `"doctor"` role using the shared service.<br>
     * * If the token is valid, fetches the prescription using the `PrescriptionService`.</p>
     * @param token JWT token of a doctor
     * @param appointmentId associated to a prescription
     * @return Returns the prescription details or an appropriate error message if validation fails.
     */
    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<Map<String, List<Prescription>>> getPrescription(
        @PathVariable("Authorization") @Valid String token,
        @PathVariable @Valid long appointmentId) {
        
        mainService.validateToken(token, "doctor");
        
        List<Prescription> pres = prescriptionService.getPrescription(appointmentId);
        logger.info("{}getPrescription:: {}", MessageHead.SUCCESS.compose(),
            "Prescriptions found for Appointment ID: " + appointmentId);
        return composeResponse(HttpStatus.OK, "prescriptions", pres);
    }
}
