package com.project.back_end.services;

import com.project.back_end.exceptions.DatabaseAccessException;
import com.project.back_end.exceptions.EmailAlreadyRegisteredException;
import com.project.back_end.exceptions.ResourceCreationFailedException;
import com.project.back_end.exceptions.ResourceNotFoundException;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.utils.TimePeriodOfDay;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

import static com.project.back_end.config.EntityConstraintsConfig.*;
import static com.project.back_end.utils.outputhelpers.MessageFormatter.MessageHead;

@Service
public class DoctorService {
    private static final Logger logger = LoggerFactory.getLogger(DoctorService.class);
    private final DoctorRepository doctorRepo;
    private final AppointmentRepository appointmentRepo;
    
    @Autowired
    public DoctorService(AppointmentRepository appointmentRepo, DoctorRepository doctorRepo) {
        this.appointmentRepo = appointmentRepo;
        this.doctorRepo = doctorRepo;
    }
    
    /**
     * This method saves a new doctor record in the database, after checking if a doctor with the same email
     * already exists.
     * @param doctor d
     */
    @Transactional
    public void createDoctor(Doctor doctor) {
        if (doctorRepo.existsByEmail(doctor.getEmail()))
            throw new EmailAlreadyRegisteredException("The email " + doctor.getEmail() + "is already in use.");
        
        doctorRepo
            .save(doctor)
            .orElseThrow(() -> new ResourceCreationFailedException("Doctor cannot be saved: "));
        logger.info("{}createDoctor:: {}", MessageHead.SUCCESS.compose(), "Doctor successfully registered");
    }
    
    /**
     * Finds doctors based on partial name matching and returns the list of doctors with their available times.
     * @param name name
     * @return res entity
     */
    @Transactional
    public List<Doctor> findDoctorsByName(String name) {
        List<Doctor> docs = doctorRepo.findByNameLike(name);
        if (docs.isEmpty()) {
            logger.warn("{}findDoctorsByName:: {}", MessageHead.FAIL.compose(),
                "Doctors not found by name containing: " + name);
        } else {
            logger.info("{}findDoctorsByName:: {}", MessageHead.SUCCESS.compose(),
                "Doctors found by name containing:" + name + ", with size: " + docs.size());
        }
        return docs;
    }
    
    /**
     * Filters doctors based on their name, specialty, and availability during a specific time (AM/PM).<p>
     * * The method fetches doctors matching the name and specialty criteria, then filters them
     *   based on their availability during the specified time period.</p>
     * @param name name
     * @param specialty spec
     * @param period time of the day been AM or PM
     * @return res ent
     */
    @Transactional
    public List<Doctor> filterDoctorsByNameAndSpecialtyAndTimePeriod(String name, String specialty, TimePeriodOfDay period) {
        List<Doctor> docs = doctorRepo.findByNameAndSpecialty(name, specialty);
        if (docs.isEmpty()) {
            logger.warn("{}filterDoctorsByNameAndSpecialtyAndTimePeriod:: {}", MessageHead.FAIL.compose(),
                "Doctors not found by name like: " + name + ", specialty: " + specialty);
            return docs;
        }
        List<Doctor> filteredDocs = filterDoctorsByTimePeriod(docs, period);
        if (filteredDocs.isEmpty()) {
            logger.warn("{}filterDoctorsByNameAndSpecialtyAndTimePeriod:: {}", MessageHead.FAIL.compose(),
                "Doctors not found by name like: " + name + ", specialty: " + specialty + " at time period " + period.toString());
        } else {
            logger.info("{}filterDoctorsByNameAndSpecialtyAndTimePeriod:: {}", MessageHead.SUCCESS.compose(),
                docs.size() + " doctors found by name like: " + name + ", specialty: " + specialty
                    + " at time period " + period.toString());
        }
        return filteredDocs;
    }
    
    /**
     * Filters doctors based on their specialty and availability during a specific time period (AM/PM).<p>
     * * Fetches doctors based on the specified specialty and filters them based on their available
     *   time slots for AM/PM.</p>
     * @param specialty spec
     * @param period Time period of day AM or PM
     * @return res
     */
    @Transactional
    public List<Doctor> filterDoctorsByTimePeriodAndSpecialty(String specialty, TimePeriodOfDay period) {
        List<Doctor> docs = doctorRepo.findBySpecialtyIgnoreCase(specialty);
        if (docs.isEmpty()) {
            logger.warn("{}filterDoctorsByTimePeriodAndSpecialty:: {}", MessageHead.FAIL.compose(),
                "Doctors not found by specialty: " + specialty);
            return docs;
        }
        List<Doctor> filteredDocs = filterDoctorsByTimePeriod(docs, period);
        if (filteredDocs.isEmpty()) {
            logger.warn("{}filterDoctorsByTimePeriodAndSpecialty:: {}", MessageHead.FAIL.compose(),
                "Doctors not found by by specialty: " + specialty + " at time period " + period.toString());
        } else {
            logger.info("{}filterDoctorsByTimePeriodAndSpecialty:: {}", MessageHead.SUCCESS.compose(),
                docs.size() + " doctors found by specialty: " + specialty + " at time period " + period.toString());
        }
        return filteredDocs;
    }
    
    /**
     * Filters doctors based on their name and the specified time period (AM/PM).<p>
     * * Fetches doctors based on partial name matching and filters the results to include
     *   only those available during the specified time period.</p>
     * @param name name
     * @param period Time period of day AM or PM
     * @return List<Doctor>
     */
    @Transactional
    public List<Doctor> filterDoctorsByNameAndTimePeriod(String name, TimePeriodOfDay period) {
        List<Doctor> docs = doctorRepo.findByNameLike(name);
        if (docs.isEmpty()) {
            logger.warn("{}filterDoctorsByNameAndTimePeriod:: {}", MessageHead.FAIL.compose(),
                "Doctors not found by name like: " + name);
            return docs;
        }
        List<Doctor> filteredDocs = filterDoctorsByTimePeriod(docs, period);
        if (filteredDocs.isEmpty()) {
            logger.warn("{}filterDoctorsByNameAndTimePeriod:: {}", MessageHead.FAIL.compose(),
                "Doctors not found by name like: " + name + " at time period " + period.toString());
        } else {
            logger.info("{}filterDoctorsByNameAndTimePeriod:: {}", MessageHead.SUCCESS.compose(),
                docs.size() + " doctors found by name like: " + name + " at time period " + period.toString());
        }
        return filteredDocs;
    }
    
    /**
     * Filters doctors by name and specialty.
     * * It ensures that the resulting list of doctors matches both the name (case-insensitive) and the specified specialty.
     * @param name name
     * @param specialty specialty
     * @return List<Doctor>
     */
    @Transactional
    public List<Doctor> filterDoctorsByNameAndSpecialty(String name, String specialty) {
        List<Doctor> docs = doctorRepo.findByNameIgnoreCaseAndSpecialty(name, specialty);
        if (docs.isEmpty()) {
            logger.warn("{}filterDoctorsByNameAndSpecialty:: {}", MessageHead.FAIL.compose(),
                "Doctors not found by name like: " + name + " and specialty: " + specialty);
        } else {
            logger.info("{}filterDoctorsByNameAndSpecialty:: {}", MessageHead.SUCCESS.compose(),
                docs.size() + "Doctors found by name like: " + name + " and specialty: " + specialty);
        }
        return docs;
    }
    
    /**
     * Filters doctors based on their specialty.
     * * This method fetches all doctors matching the specified specialty and returns them.
     * @param specialty specialty
     * @return List of doctor
     */
    @Transactional
    public List<Doctor> filterDoctorsBySpecialty(String specialty) {
        List<Doctor> docs = doctorRepo.findBySpecialtyIgnoreCase(specialty);
        if (docs.isEmpty()) {
            logger.warn("{}filterDoctorsBySpecialty:: {}", MessageHead.FAIL.compose(),
                "Doctors not found by specialty: " + specialty);
        } else {
            logger.info("{}filterDoctorsBySpecialty:: {}", MessageHead.SUCCESS.compose(),
                docs.size() + "Doctors not found by specialty: " + specialty);
        }
        return docs;
    }
    
    /**
     * Filters all doctors based on their availability during a specific time period (AM/PM).
     * * The method checks all doctors' available times and returns those available during the specified time period.
     * @param period Time period of day AM or PM
     * @return List of doctor
     */
    @Transactional
    public List<Doctor> filterAllDoctorsByTimePeriod(TimePeriodOfDay period) {
        List<Doctor> docs = doctorRepo.findAll();
        if (docs.isEmpty()) {
            logger.warn("{}filterAllDoctorsByTimePeriod:: {}", MessageHead.FAIL.compose(),
                "Doctors not found");
            return docs;
        }
        
        List<Doctor> filteredDocs = filterDoctorsByTimePeriod(docs, period);
        if (filteredDocs.isEmpty()) {
            logger.warn("{}filterAllDoctorsByTimePeriod:: {}", MessageHead.FAIL.compose(),
                "Doctors not found at time period " + period.toString());
        } else {
            logger.info("{}filterAllDoctorsByTimePeriod:: {}", MessageHead.SUCCESS.compose(),
                docs.size() + " doctors found at time period " + period.toString());
        }
        return filteredDocs;
    }
    
    /**
     * Filters a list of doctors based on whether their available times match the specified time period (AM/PM).
     * * This method processes a list of doctors and their available times to return those
     *   that fit the time criteria.
     * @param docs docs
     * @param period time of the day been AM or PM
     * @return list docs
     */
    public List<Doctor> filterDoctorsByTimePeriod(List<Doctor> docs, TimePeriodOfDay period) {
        return docs
            .stream()
            .filter(doc -> doc
                .getAvailableTimes()
                    .stream()
                    .anyMatch(period::isAtThisTimeOfDay))
            .toList();
    }
    
    /**
     * Retrieves the available time slots for a specific doctor on a particular date and filters out
     * already booked slots.
     * <p>- The method uses a Database procedure, that fetches all appointments for given doctor at a particular
     * date, and calculates the availability by comparing against booked slots.</p>
     * * Ensure that the time slots are properly formatted and the available slots are correctly filtered.
     * @param id   Doctor's id
     * @param date filter date to already booked slots
     * @return List<String> available time slots properly formatted: e.g. 10:00 - 11:00, 13:00 - 14:00.
     */
    @Transactional
    public List<String> getDoctorAvailability(long id, LocalDate date) {
        if (doctorRepo.notExistsById(id)) {
            logger.warn("{}getDoctorAvailability:: {}", MessageHead.FAIL.compose(), DOCTOR_ID_NOT_EXISTS_MSG + id);
            return List.of();
        }
        if (date.isBefore(LocalDate.now())) {
            logger.warn("{}getDoctorAvailability:: {}", MessageHead.FAIL.compose(), DATE_TIME_AT_FUTURE_MSG + date);
            return List.of();
        }
        
        List<String> availableTimeSlots = doctorRepo.getDoctorAvailability(id, date);
        if (availableTimeSlots.isEmpty()) {
            logger.warn("{}getDoctorAvailability:: {}", MessageHead.FAIL.compose(),
                "Available time slots not found for doctor ID: " + id + " at date: " + date);
        } else {
            logger.info("{}getDoctorAvailability:: {}", MessageHead.SUCCESS.compose(),
                "Available time slots found: " + availableTimeSlots + " for doctor ID: " + id + " at date: " + date);
        }
        return availableTimeSlots;
    }
    
    /**
     * Fetches all doctors from the database.
     * @return List of all affiliated doctors.
     */
    @Transactional
    public List<Doctor> getDoctors() {
        List<Doctor> docs = doctorRepo.findAll();
        if (docs.isEmpty()) {
            logger.warn("{}getDoctors:: {}", MessageHead.FAIL.compose(), "Doctors not found");
        } else {
            logger.info("{}getDoctors:: {}", MessageHead.SUCCESS.compose(), "Doctors found with size: " + docs.size());
        }
        return docs;
    }
    
    /**
     * Updates an existing doctor's details in the database.
     * @param doctor data to save.
     */
    @Transactional
    public void updateDoctor(Doctor doctor) {
        if (doctorRepo.notExistsById(doctor.getId()))
            throw new ResourceNotFoundException("Doctor not exists by ID: " + doctor.getId());
        
        doctorRepo.save(doctor)
            .orElseThrow(() -> new DatabaseAccessException("Doctor update failed with ID: " + doctor.getId()));
        logger.info("{}updateDoctor:: {}", MessageHead.SUCCESS.compose(), "Doctor updated with ID: " + doctor.getId());
    }
    
    /**
     * Deletes a doctor from the system along with all appointments associated with that doctor.
     * @param id Doctor's id to be deregistered.
     */
    @Transactional
    public void deleteDoctor(long id) {
        if (doctorRepo.notExistsById(id))
            throw new ResourceNotFoundException("Doctor not exists by ID: " + id);
    
        appointmentRepo.deleteAllByDoctorId(id);
        doctorRepo.deleteById(id);
        logger.info("{}deleteDoctor:: {}", MessageHead.SUCCESS.compose(),
            "Doctor and its associated appointments successfully deleted with ID: " + id);
    }
}
