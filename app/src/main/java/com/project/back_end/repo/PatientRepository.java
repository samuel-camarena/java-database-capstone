package com.project.back_end.repo;

import com.project.back_end.models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * This repository extends `JpaRepository<Patient, Long>`, which provides basic CRUD functionality.
 * <br>- This allows the repository to perform operations like save, deleteDoctor, updateDoctor, and find
 *   without needing to implement these methods manually.
 * <br>- JpaRepository also includes features like pagination and sorting.
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    
    Optional<Patient> save(Patient patient);
    
    /**
     * This method retrieves a Patient by their email address.
     * @param email
     * @return Optional of Patient
     */
    Optional<Patient> findByEmail(String email);
    
    /**
     * This method retrieves a Patient by either their email or phone number, allowing flexibility for the search.
     * @param email
     * @param phone
     * @return Optional of Patient
     */
    Optional<Patient> findByEmailOrPhone(String email, String phone);
    
    default boolean notExistsById(Long id) {
        return !existsById(id);
    }
    
    @Transactional
    boolean existsByEmail(String email);
}

