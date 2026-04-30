package com.project.back_end.repo;

import com.project.back_end.models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * This repository extends JpaRepository<Doctor, Long>, which gives it basic CRUD functionality.
 * - This allows the repository to perform operations like save, deleteDoctor, updateDoctor, and find without
 *   needing to implement these methods manually.
 * - JpaRepository also includes features like pagination and sorting.
 */
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    
    Optional<Doctor> save(Doctor doctor);
    /**
     * This method retrieves a Doctor by their email.
     * @param email email
     * @return List of Doctors.
     */
    Optional<Doctor> findByEmail(String email);
    
    /**
     * This method retrieves a list of Doctors whose name contains the provided search string (case-sensitive).
     * - The `CONCAT('%', :name, '%')` is used to create a pattern for partial matching.
     * @param name name
     * @return List of Doctors.
     */
    @NativeQuery(value = "SELECT * FROM doctor WHERE name LIKE %?1%")
    List<Doctor> findByNameLike(String name);
    
    /**
     * This method retrieves a list of Doctors where the name contains the search string (case-insensitive) and
     * the specialty matches exactly (case-insensitive).
     * - It combines both fields for a more specific search.
     * @param name name
     * @param specialty specialty
     * @return List of Doctors.
     */
    List<Doctor> findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(String name, String specialty);
    
    /**
     * This method retrieves a list of Doctors with the specified specialty, ignoring case sensitivity.
     * //@param Doctor's specialty.
     * @return List of Doctors.
     */
    List<Doctor> findBySpecialtyIgnoreCase(String specialty);
    
    List<Doctor> findByNameAndSpecialty(String name, String specialty);
    
    List<Doctor> findByNameIgnoreCaseAndSpecialty(String name, String specialty);
    
    @NativeQuery(value = "SELECT * FROM doctor d, doctor_available_times dat WHERE TIME(dat.available_times) = TIME(?1) AND dat.doctor_id = d.id")
    List<Doctor> findByAvailableTimesEqualsTime(String timePeriod);
    
    //@Transcient
    default boolean notExistsById(Long id) {
        return !existsById(id);
    }
    
    @Transactional
    boolean existsByEmail(String email);
    
    default boolean notExistsByEmail(String email) {
        return !existsByEmail(email);
    }
    
    /**
     * Retrieves the available time slots for a specific doctor on a particular date and filters out
     * already booked slots.
     * @param doctorId doctor to fetch all time slots for a given date, filtering out those already booked.
     * @param date objective of booking patient
     * @return List contains all available time slots in format (hh:mm), represented by
     * the starting time slot and its ending (1 plus hour). e.g.: "10:00-11:00", "14:00-15:00", ...
     */
    @Transactional
    @Procedure("GetDoctorAvailableTimeSlots")
    List<String> getDoctorAvailability(long doctorId, LocalDate date);
}