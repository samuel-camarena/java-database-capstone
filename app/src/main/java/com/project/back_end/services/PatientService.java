package com.project.back_end.services;

import com.project.back_end.DTO.DtoMapper;
import com.project.back_end.exceptions.EmailAlreadyRegisteredException;
import com.project.back_end.exceptions.ResourceCreationFailedException;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.utils.outputhelpers.MessageFormatter.MsgHeader;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * * Use of DTOs (Data Transfer Objects):
 *   * The service uses `AppointmentDTO` to transfer appointment-related data between layers. This ensures
 *     that sensitive or unnecessary data (e.g., password or private patient information)
 *     is not exposed in the response.
 */
@Service
public class PatientService {
    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);
    private final TokenService tokenService;
    private final PatientRepository patientRepo;
    private final AppointmentRepository appointmentRepo;
    private final DtoMapper dtoMapper;
    
    @Autowired
    public PatientService(PatientRepository patientRepo, AppointmentRepository appointmentRepo, TokenService tokenService, DtoMapper dtoMapper) {
        this.patientRepo = patientRepo;
        this.appointmentRepo = appointmentRepo;
        this.tokenService = tokenService;
        this.dtoMapper = dtoMapper;
    }
    
    /**
     * Creates a new patient in the database. It saves the patient object using the `PatientRepository`.
     * * If the patient is successfully saved, the method returns `1`; otherwise, it logs the error and returns `0`.
     */
    @Transactional
    public void createPatient(Patient patient) {
        if (patientRepo.existsByEmail(patient.getEmail()))
            throw new EmailAlreadyRegisteredException("The email " + patient.getEmail() + "is already in use.");
        
        patientRepo
            .save(patient)
            .orElseThrow(() -> new ResourceCreationFailedException("Patient cannot be saved: "));
        logger.info("{}createPatient:: {}", MsgHeader.SUCCESS.compose(), "Patient successfully registered");
    }
    
    /**
     * Retrieves patient details using the `tokenService` to extract the patient's email from
     * the provided token. Once the email is extracted, it fetches the corresponding patient.
     * @param token JWT
     * @return Optional<Patient>
     */
    @Transactional
    public Optional<Patient> getPatientDetails(String token) {
        String email = tokenService.extractEmail(token);
        if (email.isBlank()) {
            logger.warn("{}getPatientDetails:: {}", MsgHeader.FAIL.compose(),
                "Fail getting patient details by wrong email: " + email);
            return Optional.empty();
        }
        
        Optional<Patient> patient = patientRepo.findByEmail(email);
        patient.ifPresentOrElse(
            p -> logger.info("{}getPatientDetails:: {}", MsgHeader.SUCCESS.compose(),
                "Patient found by email: " + email),
            () -> logger.warn("{}getPatientDetails:: {}", MsgHeader.FAIL.compose(),
                "Patient not found by email: " + email));
        return patient;
    }
    
    /**
     * Retrieves a list of appointments for a specific patient, based on their ID.
     * @param patientId ID
     * @return r
     */
    @Transactional
    public List<Appointment> getPatientAppointment(long patientId) {
        List<Appointment> appoints = appointmentRepo.findByPatientId(patientId);
        if (appoints.isEmpty()) {
            logger.warn("{}getPatientAppointment:: {}", MsgHeader.FAIL.compose(),
                "Appointments not found by patient ID: " + patientId);
        } else {
            logger.info("{}getPatientAppointment:: {}", MsgHeader.SUCCESS.compose(),
                appoints.size() + " Appointments found by patient ID: " + patientId);
        }
        return appoints;
    }
    
    /**
     * Filters appointments for a patient based on the condition (e.g., "past" or "future").
     * * Retrieves appointments with a specific status 0 (scheduled) or 1 (completed) for the patient.
     * @param patientId ID
     * @param status 0 (scheduled) or 1 (completed)
     * @return r
     */
    @Transactional
    public List<Appointment> filterAppointmentsByStatus(long patientId, int status) {
        List<Appointment> appoints = appointmentRepo
            .findByPatient_IdAndStatusOrderByAppointmentTimeAsc(patientId, status);
        if (appoints.isEmpty()) {
            logger.warn("{}filterAppointmentsByStatus:: {}", MsgHeader.FAIL.compose(),
                "Appointments not found for patient ID: " + patientId + " and status: " + status);
        } else {
            logger.info("{}filterAppointmentsByStatus:: {}", MsgHeader.SUCCESS.compose(),
                appoints.size() + " appointments found for patient ID: " + patientId + " and status: " + status);
        }
        return appoints;
    }

    /**
     * Filters appointments for a patient based on the doctor's name.
     * * It retrieves appointments where the doctor’s name matches the given value,
     *   and the patient ID matches the provided ID.
     * @param patientId ID
     * @param doctorName name
     * @return List of appointment found
     */
    @Transactional
    public List<Appointment> filterAppointmentsByDoctor(long patientId, String doctorName) {
        List<Appointment> appoints = appointmentRepo.findByDoctorNameAndPatientId(doctorName, patientId);
        if (appoints.isEmpty()) {
            logger.warn("{}filterAppointmentsByDoctor:: {}", MsgHeader.FAIL.compose(),
                "Appointments not found for patient ID: " + patientId + " and doctor name: " + doctorName);
        } else {
            logger.info("{}filterAppointmentsByDoctor:: {}", MsgHeader.SUCCESS.compose(),
                appoints.size() + " appointments found for patient ID: " + patientId + " and doctor name: " + doctorName);
        }
        return appoints;
    }
    
    /**
     * Filters appointments based on both the doctor's name and the status (scheduled or completed) for a specific patient.
     * * This method combines filtering by doctor name and appointment status 0 (scheduled) or 1 (completed).
     * @param patientId ID
     * @param doctorName name
     * @param status 0 (scheduled) or 1 (completed)
     * @return List of appointment found
     */
    @Transactional
    public List<Appointment> filterAppointmentsByDoctorAndCondition(long patientId, String doctorName, int status) {
        List<Appointment> appoints = appointmentRepo
            .findByDoctorNameAndPatientIdAndStatus(doctorName, patientId, status);
        if (appoints.isEmpty()) {
            logger.warn("{}filterAppointmentsByDoctorAndCondition:: {}", MsgHeader.FAIL.compose(),
                "Appointments not found for patient ID: " + patientId + ", doctor name: " + doctorName
                    + " and status: " + status);
        } else {
            logger.info("{}filterAppointmentsByDoctorAndCondition:: {}", MsgHeader.SUCCESS.compose(),
                appoints.size() + " appointments found for patient ID: " + patientId + ", doctor name: " + doctorName
                    + " and status: " + status);
        }
        return appoints;
    }
}
