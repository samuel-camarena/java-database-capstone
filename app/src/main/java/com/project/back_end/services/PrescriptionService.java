package com.project.back_end.services;

import com.project.back_end.exceptions.ResourceNotFoundException;
import com.project.back_end.models.Prescription;
import com.project.back_end.repo.PrescriptionRepository;
import com.project.back_end.utils.outputhelpers.MessageFormatter;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.project.back_end.utils.AppHelper.composeResponse;
import static com.project.back_end.utils.OperationStatus.*;
import static com.project.back_end.utils.OperationStatus.SERVER_ERR;

@Service
public class PrescriptionService {
    private static final Logger logger = LoggerFactory.getLogger(PrescriptionService.class);
    @Autowired
    private final PrescriptionRepository prescriptionRepo;
    
    public PrescriptionService(PrescriptionRepository prescriptionRepo) {
        this.prescriptionRepo = prescriptionRepo;
    }
    
    /**
     * This method saves a new prescription to the database.
     * - Before saving, it checks if a prescription already exists for the same appointment (using the appointment ID).
     * - If a prescription exists, it returns a `400 Bad Request` with a message stating the prescription already exists.
     * - If no prescription exists, it saves the new prescription and returns a `201 Created` status with a success message.
     * @param prescription p
     * @return r
     */
    @Transactional
    public ResponseEntity<Map<String, Integer>> savePrescription(Prescription prescription) {
        try {
            if (!prescriptionRepo.findByAppointmentId(prescription.getAppointmentId()).isEmpty()){
                logger.error("{}savePrescription::", MessageFormatter.MessageHead.FAIL.compose());
                return composeResponse(FAIL, "status", FAIL.getStatus());
            }
            
            prescriptionRepo.save(prescription);
            logger.info("{}savePrescription::", MessageFormatter.MessageHead.SUCCESS.compose());
            return composeResponse(HttpStatus.CREATED, "status", null);
        } catch (Exception e) {
            logger.error("{}registerDoctor::", MessageFormatter.MessageHead.ERROR.compose());
            return composeResponse(SERVER_ERR, "status", SERVER_ERR.getStatus());
        }
    }
    
    /**
     * Retrieves a prescription associated with a specific appointment based on the `appointmentId`.
     * * If a prescription is found, it returns it within a map wrapped in a `200 OK` status.
     * * If there is an error while fetching the prescription, it throws and ResourceNotFoundException`
     * with HttpStatus code `500 Internal Server Error`, handled by the GlobalExceptionHandler,
     * logs the error, and adds an error message.
     * @param appointmentId long
     * @return List of Prescriptions found.
     */
    @Transactional
    public List<Prescription> getPrescription(long appointmentId) {
        List<Prescription> prescriptions = prescriptionRepo.findByAppointmentId(appointmentId);
        if (prescriptions.isEmpty())
            throw new ResourceNotFoundException("Prescriptions not found for Appointment ID: " + appointmentId);
        
        logger.info("{}getPrescription:: {}", MessageFormatter.MessageHead.SUCCESS.compose(),
            "Prescriptions found for Appointment ID: " + appointmentId);
        return prescriptions;
    }
}
