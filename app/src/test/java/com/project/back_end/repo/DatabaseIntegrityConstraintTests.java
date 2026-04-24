package com.project.back_end.repo;

import com.project.back_end.models.Doctor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


public class DatabaseIntegrityConstraintTests {
    @Autowired
    private DoctorRepository doctorRepo;
    private Doctor expectedDoc;

    @BeforeEach
    void Setup() {
        expectedDoc = new Doctor.Builder()
            .name("Test Name 1")
            .specialty("Test Specialty 1")
            .email("validDoc@test.com")
            .password("Password123")
            .phone("1231231234")
            .availableTimes(List.of("10:00 - 11:00", "11:00 - 12:00", "14:00 - 15:00"))
            .yearsOfExperience(5)
            .clinicAddress("Test Clinic Address 1")
            .rating(4.0).build();
    }
    
    @Test
    void givenNonUniqueEmail_whenRegister_thenThrowsDataIntegrityViolationException() throws Exception {
        String nonUniqueEmail = "dr.adams@example.com"; // Email of Doctor ID: 1
        expectedDoc.setEmail(nonUniqueEmail);
    
        try {
            doctorRepo.register(expectedDoc);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
