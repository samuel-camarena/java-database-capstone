package com.project.back_end.services;

import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

import static com.project.back_end.config.EntityConstraintsConfig.*;
import static com.project.back_end.utils.AppHelper.composeResponse;
import static com.project.back_end.utils.AppHelper.logMsg;
import static com.project.back_end.utils.OperationStatus.*;
import static com.project.back_end.utils.outputhelpers.MessageFormatter.MessageHead;

@Service()
public class DoctorService {
    private static final Logger logger = LoggerFactory.getLogger(DoctorService.class);
    @Autowired
    private final DoctorRepository doctorRepo;
    @Autowired
    private final AppointmentRepository appointmentRepo;
    @Autowired
    private final TokenService tokenService;
    
    public DoctorService(TokenService tokenService, AppointmentRepository appointmentRepo, DoctorRepository doctorRepo) {
        this.tokenService = tokenService;
        this.appointmentRepo = appointmentRepo;
        this.doctorRepo = doctorRepo;
    }
    
    /**
     * Retrieves the available time slots for a specific doctor on a particular date and filters out
     * already booked slots.
     * <p>- The method uses a Database procedure, that fetches all appointments for given doctor at a particular
     * date, and calculates the availability by comparing against booked slots.</p>
     * - Ensure that the time slots are properly formatted and the available slots are correctly filtered.
     * @param id Doctor's id
     * @param date filter date to already booked slots
     * @return List<String> available time slots properly formatted: e.g. 10:00 - 11:00, 13:00 - 14:00.
     */
    @Transactional
    public ResponseEntity<Map<String, List<String>>> getDoctorAvailability(long id, LocalDate date) {
        try {
            if (doctorRepo.notExistsById(id)) {
                logger.error("{}getDoctorAvailability:: {}", MessageHead.FAIL.compose(), DOCTOR_ID_NOT_EXISTS_MSG + id);
                return composeResponse(FAIL, "availableTimes", List.of());
            }
            
            if (date.isBefore(LocalDate.now())) {
                logger.error("{}getDoctorAvailability:: {}", MessageHead.FAIL.compose(), DATE_TIME_AT_FUTURE_MSG + date);
                return composeResponse(FAIL, "availableTimes", List.of());
            }
            
            logger.info("{}getDoctorAvailability::", MessageHead.SUCCESS.compose());
            return composeResponse(SUCCESS, "availableTimes", doctorRepo.getDoctorAvailability(id, date));
        } catch (Exception e) {
            logger.error("{}getDoctorAvailability::", MessageHead.ERROR.compose());
            return composeResponse(SERVER_ERR, "availableTimes", List.of());
        }
    }
    
    /**
     * Fetches all doctors from the database.
     * @return List of all affiliated doctors.
     */
    @Transactional
    public ResponseEntity<Map<String, List<Doctor>>> getDoctors() {
        try {
            logger.info("{}getDoctors::", MessageHead.SUCCESS.compose());
            return composeResponse(SUCCESS, "doctors", doctorRepo.findAll());
        } catch (Exception e) {
            logger.error("{}getDoctors::", MessageHead.ERROR.compose());
            return composeResponse(SERVER_ERR, "doctors", List.of());
        }
    }
    
    @Transactional
    public ResponseEntity<Map<String, Integer>> registerDoctor(Doctor doctor) {
        try {
            if (doctorRepo.existsByEmail(doctor.getEmail())){
                logger.error("{}registerDoctor::", MessageHead.FAIL.compose());
                return composeResponse(FAIL, "status", FAIL.getStatus());
            }
        
            doctorRepo.register(doctor);
            logger.info("{}registerDoctor::", MessageHead.SUCCESS.compose());
            return composeResponse(SUCCESS, "status", SUCCESS.getStatus());
        } catch (Exception e) {
            logger.error("{}registerDoctor::", MessageHead.ERROR.compose());
            return composeResponse(SERVER_ERR, "status", SERVER_ERR.getStatus());
        }
    }
    
    /**
     * Updates an existing doctor's details in the database. If the doctor doesn't exist, it returns `-1`.
     * @param doctor data to save.
     * @return returns `-1` if the doctor doesn't exist.
     */
    @Transactional // @Validate para los argumentos (`doctor`)
    public ResponseEntity<Map<String, Integer>> updateDoctor(Doctor doctor) {
        try {
            if (doctorRepo.notExistsByEmail(doctor.getEmail())) {
                logger.error("{}updateDoctor::", MessageHead.FAIL.compose());
                return composeResponse(FAIL, "status", FAIL.getStatus());
            }
            
            doctorRepo.save(doctor);
            logger.info("{}updateDoctor::", MessageHead.SUCCESS.compose());
            return composeResponse(SUCCESS, "status", SUCCESS.getStatus());
        } catch (Exception e) {
            logger.error("{}updateDoctor::", MessageHead.ERROR.compose());
            return composeResponse(SERVER_ERR, "status", SERVER_ERR.getStatus());
        }
    }
    
    /**
     * Deletes a doctor from the system along with all appointments associated with that doctor.
     * @param id Doctor's id to be deregistered.
     * @return operation status `-1`: doctor does not exist, `0`: internal error, `1`: success.
     */
    @Transactional
    public ResponseEntity<Map<String, Integer>> deleteDoctor(long id) throws Exception {
        try {
            if (doctorRepo.notExistsById(id)) {
                logger.error("{}deleteDoctor::", MessageHead.FAIL.compose());
                return composeResponse(FAIL, "status", FAIL.getStatus());
            }
        
            appointmentRepo.deleteAllByDoctorId(id);
            doctorRepo.deleteById(id);
            logger.info("{}deleteDoctor::", MessageHead.SUCCESS.compose());
            return composeResponse(SUCCESS, "status", SUCCESS.getStatus());
        } catch (Exception e) {
            logger.error("{}deleteDoctor::", MessageHead.ERROR.compose());
            return composeResponse(SERVER_ERR, "status", SERVER_ERR.getStatus());
        }
    }
    
    
    /**
     * Validates a doctor's login by checking if the email and password match an existing doctor record.
     * @param email email
     * @param password pass
     * @return It generates a token for the doctor if the login is successful, otherwise returns an error message.
     */
    @Transactional
    public ResponseEntity<Map<String, Integer>> validateDoctor(String email, String password) {
        try {
            Optional<Doctor> opDoc = doctorRepo.findByEmail(email);
            if (opDoc.isPresent()) {
                if (opDoc.get().getPassword().equals(password)) {
                    logger.info("{}validateDoctor::", MessageHead.SUCCESS.compose());
                    return composeResponse(SUCCESS, "status", SUCCESS.getStatus());
                }
                logger.error("{}validateDoctor::", MessageHead.FAIL.compose());
                return composeResponse(FAIL, "status", FAIL.getStatus());
            }
            logger.error("{}validateDoctor::", MessageHead.FAIL.compose());
            return composeResponse(FAIL, "status", FAIL.getStatus());
        } catch (Exception e) {
            logger.error("{}validateDoctor::", MessageHead.ERROR.compose());
            return composeResponse(SERVER_ERR, "status", SERVER_ERR.getStatus());
        }
    }
    
    /**
     * Finds doctors based on partial name matching and returns the list of doctors with their available times.
     * @param name name
     * @return res entity
     */
    @Transactional
    public ResponseEntity<Map<String, List<Doctor>>> findDoctorsByName(String name) {
        try {
            logger.info("{}findDoctorsByName::", MessageHead.SUCCESS.compose());
            return composeResponse(SUCCESS, "doctors", doctorRepo.findByNameLike(name));
        } catch (Exception e) {
            logger.error("{}findDoctorsByName::", MessageHead.ERROR.compose());
            return composeResponse(SERVER_ERR, "doctors", List.of());
        }
    }
    
    /**
     * Filters doctors based on their name, specialty, and availability during a specific time (AM/PM).
     * - The method fetches doctors matching the name and specialty criteria, then filters them
     *   based on their availability during the specified time period.
     * @param name name
     * @param specialty spec
     * @param timePeriod time
     * @return res ent
     */
    @Transactional
    public ResponseEntity<Map<String, List<Doctor>>> filterDoctorsByNameAndSpecialtyAndTime(String name, String specialty, String timePeriod) {
        try {
            List<Doctor> docs = doctorRepo.findByNameAndSpecialty(name, specialty);
            List<Doctor> filteredDocs = filterDoctorsByTime(docs, timePeriod);
            
            if (filteredDocs.isEmpty()) {
                logger.error("{}filterDoctorsByNameAndSpecialtyAndTime::", MessageHead.FAIL.compose());
                return composeResponse(FAIL, "doctors", List.of());
            }
            logger.info("{}filterDoctorsByNameAndSpecialtyAndTime::", MessageHead.SUCCESS.compose());
            return composeResponse(SUCCESS, "doctors", filteredDocs);
        } catch (Exception e) {
            logger.error("{}filterDoctorsByNameAndSpecialtyAndTime::", MessageHead.ERROR.compose());
            return composeResponse(SERVER_ERR, "doctors", List.of());
        }
    }
    
    /**
     * Filters doctors based on their name and the specified time period (AM/PM).
     * - Fetches doctors based on partial name matching and filters the results to include
     *   only those available during the specified time period.
     * @param name name
     * @param timePeriod time period
     * @return res ent
     */
    @Transactional
    public ResponseEntity<Map<String, List<Doctor>>> filterDoctorsByNameAndTime(String name, String timePeriod) {
        try {
            List<Doctor> docs = doctorRepo.findByNameLike(name);
            if (docs.isEmpty()) {
                logger.error("{}filterDoctorsByNameAndTime::", MessageHead.FAIL.compose());
                return composeResponse(FAIL, "doctors", Collections.emptyList());
            }
            List<Doctor> filteredDocs = filterDoctorsByTime(docs, timePeriod);
            if (filteredDocs.isEmpty()) {
                logger.error("{}filterDoctorsByNameAndTime::", MessageHead.FAIL.compose());
                return composeResponse(FAIL, "doctors", Collections.emptyList());
            }
            logger.info("{}filterDoctorsByNameAndTime::", MessageHead.SUCCESS.compose());
            return composeResponse(SUCCESS, "doctors", filteredDocs);
        } catch (Exception e) {
            logger.error("{}filterDoctorsByNameAndTime::", MessageHead.ERROR.compose());
            return composeResponse(SERVER_ERR, "doctors", Collections.emptyList());
        }
    }
    
    /**
     * Filters doctors by name and specialty.
     * - It ensures that the resulting list of doctors matches both the name (case-insensitive) and the specified specialty.
     * @param name name
     * @param specialty specialty
     * @return res
     */
    @Transactional
    public ResponseEntity<Map<String, List<Doctor>>> filterDoctorsByNameAndSpecialty(String name, String specialty) {
        try {
            List<Doctor> docs = doctorRepo.findByNameIgnoreCaseAndSpecialty(name, specialty);
            if (docs.isEmpty()) {
                logger.error("{}filterDoctorsByNameAndSpecialty::", MessageHead.FAIL.compose());
                return composeResponse(FAIL, "doctors", Collections.emptyList());
            }
            logger.info("{}filterDoctorsByNameAndSpecialty::", MessageHead.SUCCESS.compose());
            return composeResponse(SUCCESS, "doctors", docs);
        } catch (Exception e) {
            logger.error("{}filterDoctorsByNameAndSpecialty::", MessageHead.ERROR.compose());
            return composeResponse(SERVER_ERR, "doctors", Collections.emptyList());
        }
    }
    
    /**
     * Filters doctors based on their specialty and availability during a specific time period (AM/PM).
     * - Fetches doctors based on the specified specialty and filters them based on their available
     *   time slots for AM/PM.
     * @param specialty spec
     * @param timePeriod time
     * @return res
     */
    @Transactional
    public ResponseEntity<Map<String, List<Doctor>>> filterDoctorsByTimeAndSpecialty(String specialty, String timePeriod) {
        try {
            List<Doctor> docs = doctorRepo.findBySpecialtyIgnoreCase(specialty);
            if (docs.isEmpty()) {
                logger.error("{}filterDoctorsByTimeAndSpecialty::", MessageHead.FAIL.compose());
                return composeResponse(FAIL, "doctors", Collections.emptyList());
            }
            List<Doctor> filteredDocs = filterDoctorsByTime(docs, timePeriod);
            if (filteredDocs.isEmpty()) {
                logger.error("{}filterDoctorsByTimeAndSpecialty::", MessageHead.FAIL.compose());
                return composeResponse(FAIL, "doctors", Collections.emptyList());
            }
            logger.info("{}filterDoctorsByTimeAndSpecialty::", MessageHead.SUCCESS.compose());
            return composeResponse(SUCCESS, "doctors", filteredDocs);
        } catch (Exception e) {
            logger.error("{}filterDoctorsByTimeAndSpecialty::", MessageHead.ERROR.compose());
            return composeResponse(SERVER_ERR, "doctors", Collections.emptyList());
        }
    }
    
    /**
     * Filters doctors based on their specialty.
     * - This method fetches all doctors matching the specified specialty and returns them.
     * @param specialty specialty
     * @return res
     */
    @Transactional
    public ResponseEntity<Map<String, List<Doctor>>> filterDoctorsBySpecialty(String specialty) {
        try {
            List<Doctor> docs = doctorRepo.findBySpecialtyIgnoreCase(specialty);
            if (docs.isEmpty()) {
                logger.error("{}filterDoctorsBySpecialty::", MessageHead.FAIL.compose());
                return composeResponse(FAIL, "doctors", Collections.emptyList());
            }
            logger.info("{}filterDoctorsBySpecialty::", MessageHead.SUCCESS.compose());
            return composeResponse(SUCCESS, "doctors", docs);
        } catch (Exception e) {
            logger.error("{}filterDoctorsBySpecialty::", MessageHead.ERROR.compose());
            return composeResponse(SERVER_ERR, "doctors", Collections.emptyList());
        }
    }
    
    /**
     * Filters all doctors based on their availability during a specific time period (AM/PM).
     * - The method checks all doctors' available times and returns those available during the specified time period.
     * @param timePeriod time
     * @return res
     */
    @Transactional
    public ResponseEntity<Map<String, List<Doctor>>> filterDoctorsByTime(String timePeriod) {
        try {
            List<Doctor> docs = doctorRepo.findByAvailableTimesEqualsTimePeriod(timePeriod);
            if (docs.isEmpty()) {
                logger.error("{}filterDoctorsByTime::", MessageHead.FAIL.compose());
                return composeResponse(FAIL, "doctors", Collections.emptyList());
            }
            logger.info("{}filterDoctorsByTime::", MessageHead.SUCCESS.compose());
            return composeResponse(SUCCESS, "doctors", docs);
        } catch (Exception e) {
            logger.error("{}filterDoctorsByTime::", MessageHead.ERROR.compose());
            return composeResponse(SERVER_ERR, "doctors", Collections.emptyList());
        }
    }
    
    /**
     * Filters a list of doctors based on whether their available times match the specified time period (AM/PM).
     * - This method processes a list of doctors and their available times to return those
     *   that fit the time criteria.
     * @param docs docs
     * @param amOrPm time period
     * @return list docs
     */
    //private List<Doctor> filterDoctorsByTime(List<Doctor> docs, TimeOfDay amOrPm) {
    public List<Doctor> filterDoctorsByTime(List<Doctor> docs, String amOrPm) {
        logger.info("{}filterDoctorsByTime::", MessageHead.SUCCESS.compose());
        logMsg("filterDoctorsByTime", SUCCESS, "filtering doctors with Time of day:" + amOrPm, Collections.emptyList());
        return docs
            .stream()
            .filter(doc -> doc.getAvailableTimes().contains(amOrPm))
            //.filter(doc -> doc.getAvailableTimes().forEach(timePeriod -> amOrPm.isInsideTimeDayRange(timePeriod)))
            .toList();
    }
}
