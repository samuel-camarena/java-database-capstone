package com.project.back_end.services;

import com.project.back_end.exceptions.*;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.utils.TimePeriodOfDay;
import com.project.back_end.utils.outputhelpers.MessageFormatter.MsgHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.project.back_end.models.Doctor.isTimeSlotAvailable;

@Service("MainService")
public class MainService {
    private static final Logger logger = LoggerFactory.getLogger(MainService.class);
    private final TokenService tokenService;
    private final PatientRepository patientRepo;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final AdminRepository adminRepo;
    private final DoctorRepository doctorRepo;
    
    public MainService(TokenService tokenService, PatientRepository patientRepo, PatientService patientService,
                       DoctorService doctorService, AdminRepository adminRepo, DoctorRepository doctorRepo) {
        this.tokenService = tokenService;
        this.patientRepo = patientRepo;
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.adminRepo = adminRepo;
        this.doctorRepo = doctorRepo;
    }
    
    /**
     * This method checks if the provided JWT token is valid for a specific user. It uses the TokenService
     * to perform the validation.
     * * If the token is invalid or expired, it returns a 401 Unauthorized response with an appropriate
     * error message. This ensures security by preventing unauthorized access to protected resources.
     *
     * @param token t
     * @param user  u
     */
    public void validateToken(String token, String user) {
        if (!tokenService.isValidToken(token, user)) {
            throw new InvalidJwtTokenException("Invalid authentication by JWT token for user role: "
                + user + " with JWT token: " + token);
        } else {
            logger.info("{}isValidToken:: {}", MsgHeader.SUCCESS.compose(),
                "Valid JWT token for user role: " + user + " with JWT token: " + token);
        }
    }
    
    /**
     * This method checks whether a patient with the same email or phone number already exists in the system.
     * @param patient Valid `patient` object to validate
     * @return true if patient's email and phone are available for new registration, otherwise returns false.
     */
    public boolean isValidPatient(Patient patient) {
        if (patientRepo.findByEmailOrPhone(patient.getEmail(), patient.getPhone()).isPresent()) {
            logger.warn("{}isValidPatient:: {}", MsgHeader.FAIL.compose(),
                "Invalid patient for new registration with email: " + patient.getEmail() + " or phone: "
                    + patient.getPhone() + " already registered.");
            return false;
        }
        
        logger.info("{}isValidPatient:: {}", MsgHeader.SUCCESS.compose(),
            "Valid patient for new registration with email: " + patient.getEmail() + " and phone: " + patient.getPhone());
        return true;
    }
    
    /**
     * This method validates if the requested appointment time for a doctor is available. This logic prevents
     * overlapping or invalid appointment bookings.<p>
     * * It first checks if the doctor exists in the repository.<br>
     * * Then, it retrieves the list of available time slots for the doctor on the specified date.<br>
     * * It compares the requested appointment time with the start times of these slots.</p>
     *
     * @param appoint a
     * @return 1 if a match is found (valid appointment time), 0 no matching time slot found,
     * and -1 if doctor does not exist.
     */
    public boolean isValidAppointment(Appointment appoint) {
        List<String> availableTimeSlots = doctorService
            .getDoctorAvailability(appoint.getDoctor().getId(), appoint.getAppointmentDateOnly());
        
        if (availableTimeSlots.isEmpty()) {
            logger.warn("{}isValidAppointment:: {}", MsgHeader.FAIL.compose(),
                "Invalid appointment with doctor ID: " + appoint.getDoctor().getId()
                    + ", without available time slots at date: " + appoint.getAppointmentDateOnly());
            return false;
        }
        
        if (!isTimeSlotAvailable(appoint.getAppointmentTime().toString(), availableTimeSlots)) {
            logger.warn("{}isValidAppointment:: {}", MsgHeader.FAIL.compose(),
                "Doctor's time slots not available for doctor's ID: " + appoint.getDoctor().getId()
                    + " at date: " + appoint.getAppointmentDateOnly());
            return false;
        } else {
            logger.info("{}isValidAppointment:: {}", MsgHeader.SUCCESS.compose(),
                "Doctor's time slots available for doctor's ID: " + appoint.getDoctor().getId()
                    + " at date: " + appoint.getAppointmentDateOnly());
            return true;
        }
    }
    
    /**
     * This method validates the login credentials for an admin user. Ensures that only valid `admin` users
     * can access secured parts of the system.<p>
     * * It first searches the admin repository using the provided username.<br>
     * * If an admin is found, it checks if the password matches.<br>
     * * If the password is correct, it generates and returns a JWT token (using the admin’s username)<br>
     * * If the password is incorrect or admin is not found throws a `CustomCredentialNotFoundException`.<br>
     * * If any unexpected error occurs during the process, throws an 'InternalServerErrorException`.</p>
     * @param username username
     * @param password password
     * @return returns a JWT token (using the admin’s username)
     */
    public String validateAdminLogin(String username, String password) {
        Optional<Admin> opAdmin = adminRepo.findByUsername(username);
        if (opAdmin.isEmpty())
            throw new CustomCredentialNotFoundException("Wrong username for Admin login");
        if (!opAdmin.get().getPassword().equals(password))
            throw new CustomCredentialNotFoundException("Wrong password for Admin login");
        
        String token = tokenService.generateToken(opAdmin.get().getUsername());
        logger.info("{}validateAdminLogin:: {}", MsgHeader.SUCCESS.compose(),
            "Login Admin success with username: " + username);
        return token;
    }
    
    /**
     * This method handles login validation for patient users. Ensures only legitimate patients can log in
     * and access their data securely.<p>
     * * It looks up the patient by email.<br>
     * * If found, it checks whether the provided password matches the stored one.</p>
     * @param email of patient
     * @param password of patient
     * @return On successful validation, it generates a JWT token and returns it with a 200 OK status. If
     * the password is incorrect or the patient doesn't exist, it returns a 401 Unauthorized with a relevant error.
     * If an exception occurs, it returns a 500 Internal Server Error.
     */
    public String validatePatientLogin(String email, String password) {
        Optional<Patient> patient = patientRepo.findByEmail(email);
        if (patient.isEmpty())
            throw new CustomCredentialNotFoundException("Wrong email for Patient login");
        if (!patient.get().getPassword().equals(password))
            throw new CustomCredentialNotFoundException("Wrong password for Patient login");
        
        String token = tokenService.generateToken(patient.get().getEmail());
        logger.info("{}validatePatientLogin:: {}", MsgHeader.SUCCESS.compose(),
            "Login Patient success with email: " + email);
        return token;
    }
    
    /**
     * Validates a doctor's login by checking if the email and password match an existing doctor record.
     * @param email email
     * @param password pass
     * @return It generates a token for the doctor if the login is successful, otherwise returns an error message.
     */
    public String validateDoctorLogin(String email, String password) {
        Optional<Doctor> doc = doctorRepo.findByEmail(email);
        if (doc.isEmpty())
            throw new CustomCredentialNotFoundException("Wrong email for Doctor login");
        if (!doc.get().getPassword().equals(password))
            throw new CustomCredentialNotFoundException("Wrong password for Doctor login");
        
        String token = tokenService.generateToken(doc.get().getEmail());
        logger.info("{}validateDoctorLogin:: {}", MsgHeader.SUCCESS.compose(),
            "Login Doctor success with email: " + email);
        return token;
    }
    
    /**
     * This method filters a patient's appointment history based on status and doctor name.
     * This flexible method supports patient-specific querying and enhances user experience on the client side.<p>
     * * It extracts the email from the JWT token to identify the patient.<br>
     * * Depending on which filters (status, doctor name) are provided, it delegates the filtering logic to PatientService.</p>
     * @param token of type JWT for role patient.
     * @param status 0 (scheduled) or 1 (completed)
     * @param doctorName n
     * @return If no filters are provided, it retrieves all appointments for the patient.
     */
    public List<Appointment> filterPatient(String token, int status, String doctorName) {
        String email = tokenService.extractEmail(token);
        Optional<Patient> patient = patientRepo.findByEmail(email);
        if (patient.isEmpty()) {
            logger.warn("{}filterPatient:: {}", MsgHeader.FAIL.compose(),
                "JWT token email not match any patient's while filtering appointments");
            return List.of();
        }
        long patientId = patient.get().getId();
        
        if (status == 0 || status == 1) {
            if (!doctorName.equals("null"))
                return patientService.filterAppointmentsByDoctorAndCondition(patientId, doctorName, status);
            return patientService.filterAppointmentsByStatus(patientId, status);
        } else {
            if (!doctorName.equals("null"))
                return patientService.filterAppointmentsByDoctor(patientId, doctorName);
            return patientService.getPatientAppointment(patientId);
        }
    }
    
    /**
     * This method provides filtering functionality for doctors based on name, specialty, and available time slots.
     * This flexible filtering mechanism allows the frontend or consumers of the API to search and
     * narrow down doctors based on user criteria.<p>
     * * It supports various combinations of the three filters.<br>
     * * If none of the filters are provided, it returns all available doctors.</p>
     * @param name doctor's name
     * @param specialty doctor's specialty
     * @param period TimePeriodOfDay
     * @return List of doctors
     */
    public List<Doctor> filterDoctor(String name, String specialty, TimePeriodOfDay period) {
        if (!name.equals("null")) {
            if (!specialty.equals("null")) {
                if (period != null)
                    return doctorService.filterDoctorsByNameAndSpecialtyAndTimePeriod(name, specialty, period);
                return doctorService.filterDoctorsByNameAndSpecialty(name, specialty);
            } else {
                if (period != null)
                    return doctorService.filterDoctorsByNameAndTimePeriod(name, period);
                return doctorService.findDoctorsByName(name);
            }
        } else {
            if (!specialty.equals("null")) {
                if (period != null)
                    return doctorService.filterDoctorsByTimePeriodAndSpecialty(specialty, period);
                return doctorService.filterDoctorsBySpecialty(specialty);
            } else {
                return doctorService.filterAllDoctorsByTimePeriod(period);
            }
        }
    }
    
    public long extractSubjectIdFromToken(String token, String user) {
        switch(user) {
            case "patient" -> {
                Optional<Patient> opPatient = patientRepo.findByEmail(tokenService.extractEmail(token));
                if (opPatient.isEmpty())
                    throw new ResourceNotFoundException("Patient not found by email: " + tokenService.extractEmail(token));
                return opPatient.get().getId();
            }
            case "doctor" -> {
                Optional<Doctor> opDoctor = doctorRepo.findByEmail(tokenService.extractEmail(token));
                if (opDoctor.isEmpty())
                    throw new ResourceNotFoundException("Doctor not found by email: " + tokenService.extractEmail(token));
                return opDoctor.get().getId();
            }
            case "admin" -> {
                Optional<Admin> opAdmin = adminRepo.findByUsername(tokenService.extractUsername(token));
                if (opAdmin.isEmpty())
                    throw new ResourceNotFoundException("Admin not found by username: " + tokenService.extractUsername(token));
                return opAdmin.get().getId();
            }
            default -> throw new IllegalStateException("Unexpected value: " + user);
        }
    }
}
