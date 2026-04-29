package com.project.back_end.services;

import com.project.back_end.exceptions.*;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.project.back_end.utils.outputhelpers.MessageFormatter.MessageHead;

/**
 * MainService layer class for handling business logic.
 */
@Service
public class AppointmentService {
    private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);
    private final TokenService tokenService;
    private final MainService mainService;
    private final DoctorService doctorService;
    private final AppointmentRepository appointmentRepo;
    private final PatientRepository patientRepo;
    
    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepo, PatientRepository patientRepo,
                              TokenService tokenService, MainService mainService, DoctorService doctorService) {
        
        this.appointmentRepo = appointmentRepo;
        this.patientRepo = patientRepo;
        this.tokenService = tokenService;
        this.mainService = mainService;
        this.doctorService = doctorService;
    }
    
    /**
     * This method is responsible for saving the new appointment to the database.<p>
     * * For future behavior extension it will return the URI of the book.</p>
     * @param appoint a
     */
    public void bookAppointment(Appointment appoint) {
        if (!mainService.isValidAppointment(appoint))
            throw new ResourceCreationFailedException("Appointment creation failed with object data: " + appoint);
        
        Optional<Appointment> opAppoint = appointmentRepo.book(appoint);
        if (opAppoint.isEmpty())
            throw new ResourceCreationFailedException("Appointment creation failed with object data: " + appoint);
        
        logger.info("{}bookAppointment:: {}", MessageHead.SUCCESS.compose(), "Appointment booked with data: " + appoint);
    }
    
    /**
     * This method retrieves a list of appointments for a specific doctor on a particular day, optionally filtered by
     * the patient's name.<p>
     * * It uses `@Transactional` to ensure that database operations are consistent and handled in a single transaction.</p>
     * @param doctorId doctorId
     * @param date date
     * @param patientName Patient's name
     * @return List of Appointments
     */
    @Transactional
    public List<Appointment> getAppointments(long doctorId, LocalDate date, String patientName) {
        List<Appointment> appoints;
        if (!patientName.equals("null")) {
            appoints = appointmentRepo
                .findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentDate(
                    doctorId, patientName, date);
        } else {
            appoints = appointmentRepo
                .findByDoctorIdAndAppointmentDate(doctorId, date);
        }
        logger.info("{}getAppointments:: {}", MessageHead.SUCCESS.compose(), "Appointments retrieve for doctor ID: "
            + doctorId + ", at day: " + date + " and optional patient name: " + patientName);
        return appoints;
    }
    
    /**
     * Extra Method for further functionality extension.
     * @param patientName p
     * @param date d
     * @return Appointment List
     */
    @Transactional
    public List<Appointment> getAppointmentsByPatientAndDate(String patientName, LocalDate date) {
        List<Appointment> appoints = appointmentRepo
            .findByPatient_NameContainingIgnoreCaseAndAppointmentDate(patientName, date);
        if (appoints.isEmpty())
            throw new ResourceNotFoundException("Appointments not found by patient name: "
                + patientName + " and date: " + date);
        
        return appoints;
    }
    
    /**
     * This method is used to update an existing appointment based on its ID.<p>
     * * It validates whether the patient ID matches, checks if the appointment is available for updating,
     *   and ensures that the doctor is available at the specified time.<br>
     * * If the updateDoctor is successful, it saves the appointment; otherwise, it throws a ResourceUpdateFailedException.
     *   error message.</p>
     * @param appoint a
     */
    @Transactional
    public void updateAppointment(Appointment appoint) {
        appointmentRepo.findById(appoint.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Appointment not found by ID: " + appoint.getId()));
        
        if (!mainService.isValidAppointment(appoint))
            throw new ResourceUpdateFailedException("Appointment update failed with object data: " + appoint);
        
        if(!patientRepo.existsById(appoint.getPatient().getId()))
            throw new ResourceNotFoundException("Appointment's patient not found by ID: " + appoint.getPatient().getId());

        appointmentRepo.save(appoint);
        logger.info("{}updateAppointment:: {}", MessageHead.SUCCESS.compose(), "Appointment updated");
    }
    
    /**
     * This method updates the status of an appointment by changing its value in the database.
     * @param id `long` appointment to be change
     * @param status `int` must be 0 (scheduled) or 1 (completed)
     */
    @Transactional
    public void updateStatus(long id, int status){
        Optional<Appointment> opAppoint = appointmentRepo.findById(id);
        if (opAppoint.isEmpty())
            throw new ResourceNotFoundException("Appointment not found by ID: " + id);
        
        opAppoint.get().setStatus(status);
        appointmentRepo.save(opAppoint.get());
        logger.info("{}updateStatus:: {}", MessageHead.SUCCESS.compose(),
            "Appointment status successfully Changed to: " + status);
    }
    
    /**
     * This method cancels an appointment by deleting it from the database.<p>
     * * It ensures the patient who owns the appointment is trying to cancel it and throws ResourceNotFoundException
     * or .</p>
     * @param id long ...
     * @param token String ...
     */
    @Transactional
    public void cancelAppointment(long id, String token){
        Appointment appoint = appointmentRepo
            .findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException("Appointment not found by ID: " + id));
        
        Patient patient = patientRepo
            .findById(appoint.getPatient().getId())
            .orElseThrow(() ->
                new ResourceNotFoundException("Patient not found by ID: " + id));
        
        String authPatientEmail = tokenService.extractEmail(token);
        if (!patient.getEmail().equalsIgnoreCase(authPatientEmail))
            throw new CredentialMismatchedException("Patient authenticated does not match appointment ownership");
        
        appointmentRepo.deleteById(id);
        logger.info("{}cancelAppointment:: {}", MessageHead.SUCCESS.compose(), "Appointment canceled by ID: "
            + id + " and patient ID: " + patient.getId());
    }
    
    public enum Status {
        SCHEDULED (0),
        COMPLETED (1),
        CANCELED (-1);
        
        private final int value;
        
        Status(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
    }
}
