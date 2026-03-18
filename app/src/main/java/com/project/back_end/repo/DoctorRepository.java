package com.project.back_end.repo;

import com.project.back_end.models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.print.Doc;
import java.util.List;
import java.util.Optional;

/**
 * This repository extends JpaRepository<Doctor, Long>, which gives it basic CRUD functionality.
 * - This allows the repository to perform operations like save, delete, update, and find without
 *   needing to implement these methods manually.
 * - JpaRepository also includes features like pagination and sorting.
 */
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    
    /**
     * This method retrieves a Doctor by their email.
     * @param email
     * @return List of Doctors.
     */
    Optional<Doctor> findByEmail(String email);
    
    /**
     * This method retrieves a list of Doctors whose name contains the provided search string (case-sensitive).
     * - The `CONCAT('%', :name, '%')` is used to create a pattern for partial matching.
     * @param name
     * @return List of Doctors.
     */
    List<Doctor> findByNameLike(String name);
    
    /**
     * This method retrieves a list of Doctors where the name contains the search string (case-insensitive) and
     * the specialty matches exactly (case-insensitive).
     * - It combines both fields for a more specific search.
     * @param name
     * @param specialty
     * @return List of Doctors.
     */
    List<Doctor> findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(String name, String specialty);
    
    /**
     * This method retrieves a list of Doctors with the specified specialty, ignoring case sensitivity.
     * //@param Doctor's specialty.
     * @return List of Doctors.
     */
    List<Doctor> findBySpecialtyIgnoreCase(String specialty);
}