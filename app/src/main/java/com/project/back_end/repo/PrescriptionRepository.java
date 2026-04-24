package com.project.back_end.repo;

import com.project.back_end.models.Prescription;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * The repository extends MongoRepository<Prescription, String>, which provides
 * basic CRUD functionality for MongoDB.
 * <br>- This allows the repository to perform operations like save, deleteDoctor, updateDoctor, and find
 * without needing to implement these methods manually.
 * <br>- MongoRepository is tailored for working with MongoDB, unlike JPARepository
 * which is used for relational databases.
 */
@Repository
public interface PrescriptionRepository  extends MongoRepository<Prescription,String> {
    
    /**
     * This method retrieves a list of prescriptions associated with a specific appointment.
     * <br>- MongoRepository automatically derives the query from the method name, in this case,
     *   it will find prescriptions by the appointment ID.
     * @param appointmentId ID of the Appointment.
     * @return List of Prescriptions
     */
    List<Prescription> findByAppointmentId(Long appointmentId);
}

