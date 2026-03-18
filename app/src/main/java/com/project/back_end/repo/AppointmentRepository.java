package com.project.back_end.repo;

import com.project.back_end.models.Appointment;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository  extends JpaRepository<Appointment, Long> {
    
    /**
     * This method retrieves a list of appointments for a specific doctor within a given time range.
     * - The doctor’s available times are eagerly fetched to avoid lazy loading.
     * - It uses a LEFT JOIN to fetch the doctor’s available times along with the appointments.
     */
    List<Appointment> findByDoctorIdAndAppointmentTimeBetween(Long doctorId, LocalDateTime start,
                                                              LocalDateTime end);
    
    /**
     * This method retrieves appointments for a specific doctor and patient name (ignoring case)
     * within a given time range.
     * - It performs a LEFT JOIN to fetch both the doctor and patient details along with the appointment times.
     */
    List<Appointment> findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
        Long doctorId, String patientName, LocalDateTime start, LocalDateTime end);
    
    /**
     * This method retrieves all appointments for a specific patient.
     */
    List<Appointment> findByPatientId(Long patientId);
    
    /**
     * This method retrieves all appointments for a specific patient with a given status, ordered by the appointment time.
     */
    List<Appointment> findByPatient_IdAndStatusOrderByAppointmentTimeAsc(Long patientId, int status);
    
    /**
     * This method retrieves appointments based on a doctor’s name (using a LIKE query) and the patient’s ID.
     */
    List<Appointment> filerByDoctorNameAndPatientId(String doctorName, Long patientId);
    
    /**
     * This method retrieves appointments based on a doctor’s name (using a LIKE query), patient’s ID, and a specific appointment status.
     */
    List<Appointment> filterByDoctorNameAndPatientIdAndStatus(String doctorName, Long patientId, int status);
    
    /**
     * This method deletes all appointments associated with a particular doctor.
     * - It is marked as @Modifying and @Transactional, which makes it a modification query,
     *   ensuring that the operation is executed within a transaction.
     * - The @Modifying annotation is used to indicate that the method performs a modification operation
     *   (like DELETE or UPDATE).
     * - The @Transactional annotation ensures that the modification is done within a transaction,
     *   meaning that if any exception occurs, the changes will be rolled back.
     */
    @Modifying
    @Transactional
    void deleteAllByDoctorId(Long doctorId);
}
