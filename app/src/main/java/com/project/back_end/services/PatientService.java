package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.DTO.DtoMapper;
import com.project.back_end.exceptions.ResourceNotFoundException;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.utils.outputhelpers.MessageFormatter.MessageHead;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.project.back_end.utils.AppHelper.composeResponse;
import static com.project.back_end.utils.OperationStatus.*;
import static com.project.back_end.utils.OperationStatus.SERVER_ERR;

/**
 * - Use of DTOs (Data Transfer Objects):
 *   - The service uses `AppointmentDTO` to transfer appointment-related data between layers. This ensures
 *     that sensitive or unnecessary data (e.g., password or private patient information)
 *     is not exposed in the response.
 * - Handling Exceptions and Errors:
 *   - The service methods handle exceptions using try-catch blocks and log any issues that occur. If an
 *     error occurs during database operations, the service responds with appropriate
 *     HTTP status codes (e.g., `500 Internal Server Error`).
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
     * - If the patient is successfully saved, the method returns `1`; otherwise, it logs the error and returns `0`.
     *
     */
    @Transactional
    public Optional<Patient> registerPatient(Patient patient) {
        try {
            if (patientRepo.existsByEmail(patient.getEmail())){
                logger.warn("{}registerPatient:: ", MessageHead.FAIL.compose());
                return composeResponse(FAIL, "status", FAIL.getStatus());
            }
            
            if (patientRepo.register(patient).isEmpty()) {
                logger.warn("{}registerPatient::{}", MessageHead.FAIL.compose(), "Repository error saving patient");
                return composeResponse(FAIL, "status", FAIL.getStatus());
            };
            
            logger.info("{}registerPatient::", MessageHead.SUCCESS.compose());
            return composeResponse(SUCCESS, "status", SUCCESS.getStatus());
        } catch (Exception e) {
            logger.error("{}registerPatient:: {}", MessageHead.ERROR.compose() + e.getMessage());
            return composeResponse(SERVER_ERR, "status", SERVER_ERR.getStatus());
        }
    }
    
    /**
     * Retrieves a list of appointments for a specific patient, based on their ID.
     * - The appointments are then converted into `AppointmentDTO` objects for easier consumption by the API client.
     * - This method is marked as `@Transactional` to ensure database consistency during the transaction.
     * @param patientId ID
     * @return r
     */
    @Transactional
    public List<AppointmentDTO> getPatientAppointment(long patientId) {
        List<Appointment> appoints = appointmentRepo.findByPatientId(patientId);
        if (appoints.isEmpty())
            throw new ResourceNotFoundException("Appointments not found by patient ID: " + patientId);
        
        List<AppointmentDTO> appointsDTO = appoints
            .stream()
            .map(dtoMapper::mapAppointmentToDTO)
            .toList();
        
        logger.info("{}getPatientAppointment:: {}", MessageHead.SUCCESS.compose(), "Success conversion appointments");
        return composeResponse(HttpStatus.OK, "appointments", appointsDTO);
    }
    
    /**
     * Filters appointments for a patient based on the condition (e.g., "past" or "future").
     * - Retrieves appointments with a specific status (0 for future, 1 for past) for the patient.
     * - Converts the appointments into `AppointmentDTO` and returns them in the response.
     * @param patientId ID
     * @param condition c
     * @return r
     */
    @Transactional
    public List<AppointmentDTO> filterAppointmentsByCondition(long patientId, String condition) {
        List<Appointment> appoints = appointmentRepo
            .findByPatient_IdAndStatusOrderByAppointmentTimeAsc(patientId, Integer.parseInt(condition));
        
        if (appoints.isEmpty()) {
            logger.warn("{}filterAppointmentsByCondition::", MessageHead.FAIL.compose());
            return composeResponse(FAIL, "appointments", Collections.emptyList());
        }
        
        List<AppointmentDTO> appointsDTO = appoints
            .stream()
            .map(this::mapAppointmentToDTO)
            .toList();
        
        logger.info("{}filterAppointmentsByCondition:: {}", MessageHead.SUCCESS.compose(), "Success filter appointments");
        return composeResponse(SUCCESS, "appointments", appointsDTO);
    }

    /**
     * Filters appointments for a patient based on the doctor's name.
     * - It retrieves appointments where the doctor’s name matches the given value,
     *   and the patient ID matches the provided ID.
     * @param patientId ID
     * @param doctorName name
     * @return r
     */
    @Transactional
    public List<AppointmentDTO> filterAppointmentsByDoctor(long patientId, String doctorName) {
        try {
            List<Appointment> appoints = appointmentRepo.findByDoctorNameAndPatientId(doctorName, patientId);
            if (appoints.isEmpty()) {
                logger.warn("{}filterAppointmentsByDoctor::", MessageHead.FAIL.compose());
                return composeResponse(FAIL, "appointments", Collections.emptyList());
            }
            
            List<AppointmentDTO> appointsDTO = appoints
                .stream()
                .map(this::mapAppointmentToDTO)
                .toList();
            
            logger.info("{}filterAppointmentsByDoctor::", MessageHead.SUCCESS.compose(), "Success found appointments");
            return composeResponse(SUCCESS, "appointments", appointsDTO);
        } catch (Exception e) {
            logger.error("{}filterAppointmentsByDoctor:: {}", MessageHead.ERROR.compose(), e.getMessage());
            return composeResponse(SERVER_ERR, "appointments", List.of());
        }
    }
    
    /**
     * Filters appointments based on both the doctor's name and the condition (past or future) for a specific patient.
     * - This method combines filtering by doctor name and appointment status (past or future).
     * - Converts the appointments into `AppointmentDTO` objects and returns them in the response.
     * @param patientId ID
     * @param doctorName name
     * @param condition condition
     * @return
     */
    @Transactional
    public List<AppointmentDTO> filterAppointmentsByDoctorAndCondition(
            long patientId, String doctorName, String condition) {
        
        try {
            List<Appointment> appoints = appointmentRepo.findByDoctorNameAndPatientIdAndStatus(
                doctorName, patientId, Integer.parseInt(condition));
            if (appoints.isEmpty()) {
                logger.warn("{}filterAppointmentsByDoctorAndCondition::", MessageHead.FAIL.compose());
                return composeResponse(FAIL, "appointments", Collections.emptyList());
            }
            
            List<AppointmentDTO> appointsDTO = appoints
                .stream()
                .map(this::mapAppointmentToDTO)
                .toList();
            
            logger.info("{}filterAppointmentsByDoctorAndCondition::", MessageHead.SUCCESS.compose(), "Success found appointments");
            return composeResponse(SUCCESS, "appointments", appointsDTO);
        } catch (Exception e) {
            logger.error("{}filterAppointmentsByDoctorAndCondition:: {}", MessageHead.ERROR.compose(), e.getMessage());
            return composeResponse(SERVER_ERR, "appointments", List.of());
        }
    }
    
    /**
     * Retrieves patient details using the `tokenService` to extract the patient's email from the provided token.
     * - Once the email is extracted, it fetches the corresponding patient from the `patientRepository`.
     * - It returns the patient's information in the response body.
     * @param token
     * @return
     */
    @Transactional
    public Optional<Patient> getPatientDetails(String token) {
        try {
            String patientEmail = tokenService.extractEmail(token);
            if (patientEmail.isEmpty()) {
                logger.warn("{}getPatientDetails::", MessageHead.FAIL.compose());
                return composeResponse(FAIL, "patient", null);
            }
            
            Optional<Patient> opPatient = patientRepo.findByEmail(patientEmail);
            if (opPatient.isEmpty()) {
                logger.warn("{}getPatientDetails::", MessageHead.FAIL.compose());
                return composeResponse(FAIL, "patient", null);
            }
    
            logger.info("{}getPatientDetails::", MessageHead.SUCCESS.compose(), "Success patient found");
            return composeResponse(SUCCESS, "patient", opPatient.get());
        } catch (Exception e) {
            logger.error("{}getPatientDetails:: {}", MessageHead.ERROR.compose(), e.getMessage());
            return composeResponse(SERVER_ERR, "patient", null);
        }
    }
}
