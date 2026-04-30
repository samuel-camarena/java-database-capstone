package com.project.back_end.controllers;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.DTO.DtoMapper;
import com.project.back_end.DTO.Login;
import com.project.back_end.exceptions.EmailOrPhoneAlreadyRegisteredException;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.services.MainService;
import com.project.back_end.services.PatientService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.project.back_end.utils.AppHelper.composeResponse;

@RestController
@RequestMapping("${api.path}" + "v1/patient")
public class PatientController {
    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);
    private final PatientService patientService;
    private final MainService mainService;
    private final DtoMapper dtoMapper;
    
    @Autowired
    public PatientController(PatientService patientService, MainService mainService, DtoMapper dtoMapper) {
        this.patientService = patientService;
        this.mainService = mainService;
        this.dtoMapper = dtoMapper;
    }
    
    /**
     * Handles HTTP GET requests to retrieve patient details using a token.<p>
     * * Validates the token for the `"patient"` role using the shared service.</p>
     * @param token JWT Authorization token
     * @return If the token is valid, returns patient information; otherwise, returns an appropriate error message.
     */
    @GetMapping("/{token}")
    public ResponseEntity<Map<String, Patient>> getPatient(@PathVariable("Authorization") @Valid String token) {
        mainService.validateToken(token, "patient");
        return composeResponse(HttpStatus.OK, "patient", patientService.getPatientDetails(token).get());
    }
    
    /**
     * Handles HTTP POST requests for patient registration.<p>
     * * Accepts a validated `Patient` object in the request body.<br>
     * * First checks if the patient already exists using the shared service.</p>
     * @param patient p
     * @return If validation passes, attempts to create the patient and returns success or error messages
     *          based on the outcome.
     */
    @PostMapping() // "/save"
    public ResponseEntity<Map<String, String>> createPatient(@RequestBody @Valid Patient patient) {
        if (!mainService.isValidPatient(patient))
            throw new EmailOrPhoneAlreadyRegisteredException(
                "The email " + patient.getEmail() + " or phone " + patient.getPhone() + " is already in use.");
        patientService.createPatient(patient);
        return composeResponse(HttpStatus.CREATED, "message", "Patient successfully registered");
    }

    /**
     * Handles HTTP POST requests for patient login.<p>
     * * Accepts a `Login` DTO containing email/username and password.<br>
     * * Delegates authentication to the `validatePatientLogin` method in the shared service.</p>
     * @param loginDTO ...
     * @return Returns a response with a token or an error message depending on login success
     */
    @GetMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody @Valid Login loginDTO) {
        return composeResponse(HttpStatus.OK, "token",
            mainService.validatePatientLogin(loginDTO.getIdentifier(), loginDTO.getPassword()));
    }
    
    /**
     * Handles HTTP GET requests to fetch appointment details for a specific patient.<p>
     * * Requires the patient ID, token, and user role as path variables.<br>
     * * Validates the token using the shared service.</p>
     * @param user u
     * @param token t
     * @param id Patient ID
     * @return If valid, retrieves the patient's appointment data from `PatientService`; otherwise, returns a validation error.
     */
    @GetMapping("/appointments/{id}")
    public ResponseEntity<Map<String, List<AppointmentDTO>>> getPatientAppointment(
        @PathVariable("X-User") @Valid String user,
        @PathVariable("Authorization") @Valid String token,
        @PathVariable @Valid long id) {

        mainService.validateToken(token, user);
        List<Appointment> appoints = patientService.getPatientAppointment(id);
        return composeResponse(HttpStatus.OK, "appointments", dtoMapper.mapAppointmentsToDTOs(appoints));
    }
    
    /**
     * Handles HTTP GET requests to filter a patient's appointments based on specific conditions.<p>
     * - Accepts filtering parameters: `condition`, `name`, and a token.<br>
     * - Token must be valid for a `"patient"` role.</p>
     * @param condition can indicate if the appointment is "Scheduled:0", "Completed:1", or other statuses (e.g., "Canceled") as needed.
     * @param name n
     * @param token t
     * @return If valid, delegates filtering logic to the shared service and returns the filtered result.
     */
    @GetMapping("/appointments/filter")
    public ResponseEntity<Map<String, List<AppointmentDTO>>> filterPatientAppointment(
        @PathVariable(required = false) String condition,
        @PathVariable(required = false) String name,
        @PathVariable("Authorization") String token) {
        
        mainService.validateToken(token, "patient");
        List<Appointment> appoints = mainService.filterPatient(token, Integer.parseInt(condition), name);
        return composeResponse(HttpStatus.OK, "appointments", dtoMapper.mapAppointmentsToDTOs(appoints));
    }
}


