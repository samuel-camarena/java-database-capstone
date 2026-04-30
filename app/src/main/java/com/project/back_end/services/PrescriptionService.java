package com.project.back_end.services;

import com.project.back_end.exceptions.ResourceCreationFailedException;
import com.project.back_end.exceptions.ResourceNotFoundException;
import com.project.back_end.models.Prescription;
import com.project.back_end.repo.PrescriptionRepository;
import com.project.back_end.utils.outputhelpers.MessageFormatter.MessageHead;
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
     * This method saves a new prescription to the database.<p>
     * * Before saving, it checks if a prescription already exists for the same appointment (using the appointment ID).</p>
     * * If no prescription exists, it saves the new prescription.
     * * If a prescription exists, it throws a ResourceCreationFailedException with a `400 Bad Request` and
     * a descriptive message.
     * @param pres p
     */
    @Transactional
    public void savePrescription(Prescription pres) {
        if (!prescriptionRepo.findByAppointmentId(pres.getAppointmentId()).isEmpty())
            throw new ResourceCreationFailedException("Prescription already associated with appointment ID: "
                + pres.getAppointmentId());
        
        prescriptionRepo.save(pres);
        logger.info("{}savePrescription:: {}", MessageHead.SUCCESS.compose(),
            "Prescription successfully saved associated to appointment with ID: " +  pres.getAppointmentId());
    }
    
    /**
     * Retrieves a prescription associated with a specific appointment based on the `appointmentId`.<p>
     * @param appointmentId long
     * @return List of Prescriptions found.
     */
    @Transactional
    public List<Prescription> getPrescription(long appointmentId) {
        List<Prescription> prescriptions = prescriptionRepo.findByAppointmentId(appointmentId);
        if (prescriptions.isEmpty()) {
            logger.warn("{}getPrescription:: {}", MessageHead.FAIL.compose(),
                "Not found Prescriptions for associated appointment with ID: " + appointmentId);
        } else {
            logger.info("{}getPrescription:: {}", MessageHead.SUCCESS.compose(),
                "Found " + prescriptions.size() + " Prescriptions for appointment with ID: " + appointmentId);
        }
        return prescriptions;
    }
}
