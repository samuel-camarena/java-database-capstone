package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.project.back_end.utils.AppHelper.composeResponse;
import static com.project.back_end.utils.OperationStatus.*;
import static com.project.back_end.utils.outputhelpers.MessageFormatter.MessageHead;

/**
 * mainService layer class for handling business logic.
 */
@Service
public class AppointmentService {
    
    private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);
    private final AppointmentRepository appointmentRepo;
    private final DoctorRepository doctorRepo;
    private final PatientRepository patientRepo;
    private final TokenService tokenService;
    private final DoctorService doctorService;
    
    public AppointmentService(AppointmentRepository appointmentRepo, DoctorRepository doctorRepo,
                              PatientRepository patientRepo, TokenService tokenService, DoctorService doctorService) {
        this.appointmentRepo = appointmentRepo;
        this.doctorRepo = doctorRepo;
        this.patientRepo = patientRepo;
        this.tokenService = tokenService;
        this.doctorService = doctorService;
    }
    
    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepo.save(appointment);
            return 1;
        } catch (Exception e) {
            System.err.println("Error :: AppointmentService :: bookAppointment :: " + e.getMessage());
            return 0;
        }
    }
    
    // 5. **Update Appointment Method**:
//    - This method is used to updateDoctor an existing appointment based on its ID.
//    - It validates whether the patient ID matches,
//      checks if the appointment is available for updating,
//      and ensures that the doctor is available at the specified time.
//    - If the updateDoctor is successful, it saves the appointment; otherwise, it returns an appropriate error message.
//    - Instruction: Ensure proper validation and error handling is included for appointment updates.
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        try {
            if(!appointmentRepo.existsById(appointment.getId())) {
                logger.error("{}updateAppointment::", MessageHead.FAIL.compose());
                return composeResponse(NOT_FOUND, "updateAppointment", "Appointment not found");
            }
            if(!patientRepo.existsById(appointment.getPatient().getId())) {
                logger.error("{}updateAppointment::", MessageHead.FAIL.compose());
                return composeResponse(NOT_FOUND, "updateAppointment", "Patient not found");
            }
            
            ResponseEntity<Map<String, List<String>>> res = doctorService
                .getDoctorAvailability(appointment.getDoctor().getId(), appointment.getAppointmentTime().toLocalDate());
            
            if (res.getStatusCode().is2xxSuccessful()) {
                if(res.getBody().get("availableTimes")
                    .stream()
                    .filter(time -> time.substring(0, 5)
                        .equals(appointment.getAppointmentTime().toLocalTime()))
                    .findFirst()
                    .isPresent()) {
                    
                    appointmentRepo.save(appointment);
                    logger.info("{}updateAppointment::", MessageHead.SUCCESS.compose());
                    return composeResponse(SUCCESS, "updateAppointment", "");
                }
            }
            
            logger.error("{}updateAppointment::", MessageHead.FAIL.compose());
            return composeResponse(FAIL, "updateAppointment", "Appointment not available");
        } catch (Exception e) {
            logger.error("{}updateAppointment::", MessageHead.ERROR.compose());
            return composeResponse(SERVER_ERR, "status", SERVER_ERR.getStatus() + "");
        }
    }
    
    // 6. **Cancel Appointment Method**:
    //    - This method cancels an appointment by deleting it from the database.
    //    - It ensures the patient who owns the appointment is trying to cancel it and handles possible errors.
    //    - Instruction: Make sure that the method checks for the patient ID match before deleting the appointment
    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token){
        Optional<Appointment> opAppoint = appointmentRepo.findById(id);
        if (opAppoint.isEmpty()) {
            logger.error("{}cancelAppointment::", MessageHead.FAIL.compose());
            return composeResponse(NOT_FOUND, "cancelAppointment", "Appointment not found");
        }
        
        Optional<Patient> opPatient = patientRepo.findById(opAppoint.get().getPatient().getId());
        if(opPatient.isEmpty()) {
            logger.error("{}cancelAppointment::", MessageHead.FAIL.compose());
            return composeResponse(NOT_FOUND, "cancelAppointment", "Patient not found");
        }
        
        // token -> extract email and check for patient matching email.
        // if correct -> delete appointment by id
        appointmentRepo.deleteById(id);
        return ResponseEntity.ok().build();
        // if fail -> return responseEntity.NotAuthorized.
        // return composeResponse(UNAUTHORIZED, "cancelAppointment", "Wrong token");
    }
    
    /**
     * This method retrieves a list of appointments for a specific doctor on a particular day,
     * optionally filtered by the patient's name.
     * @param doctorId doctorId
     * @param date date
     * @param patientsName Patient's name
     * @return List of Appointments
     */
    public ResponseEntity<Map<String, List<Appointment>>> getAppointment(
            long doctorId, LocalDateTime date, String patientsName) {
        
        try {
            List<Appointment> appoints =
                appointmentRepo.findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                    doctorId, patientsName, date, date);
            if (appoints.isEmpty()) {
                logger.error("{}getAppointment::", MessageHead.FAIL.compose());
                return composeResponse(FAIL, "appointments", appoints);
            }
            
            logger.info("{}getAppointment::", MessageHead.SUCCESS.compose());
            return composeResponse(SUCCESS, "appointments", appoints);
        } catch (Exception e) {
            logger.info("{}getAppointment::", MessageHead.SUCCESS.compose());
            return composeResponse(SERVER_ERR, "appointments", List.of());
        }
    }
    
    /**
     * This method updates the status of an appointment by changing its value in the database.
     * It should be annotated with `@Transactional` to ensure the operation is executed in a single transaction.
     * @param id
     * @param status
     * @return
     */
    @Transactional
    public ResponseEntity<Map<String, Integer>> updateStatus(long id, int status){
        try {
            Optional<Appointment> opAppoint = appointmentRepo.findById(id);
            if (opAppoint.isEmpty()) {
                logger.error("{}updateStatus::", MessageHead.FAIL.compose());
                return composeResponse(FAIL, "appointments", FAIL.getStatus());
            }
            opAppoint.get().setStatus(status);
            appointmentRepo.save(opAppoint.get());
            logger.info("{}updateStatus::", MessageHead.SUCCESS.compose());
            return composeResponse(SUCCESS, "appointments", SUCCESS.getStatus());
        } catch (Exception e) {
            logger.info("{}updateStatus::", MessageHead.ERROR.compose());
            return composeResponse(SERVER_ERR, "appointments", SERVER_ERR.getStatus());
        }
    }
}
