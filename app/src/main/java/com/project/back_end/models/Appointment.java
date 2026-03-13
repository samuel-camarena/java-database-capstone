package com.project.back_end.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
public class Appointment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @NotNull(message = "Doctor cannot be null")
    @ManyToOne
    private Doctor doctor;
    
    @NotNull(message = "Patient cannot be null")
    @ManyToOne
    private Patient patient;
    
    @NotNull(message = "Appointment cannot be null")
    @Future(message = "Appointment time must be in the future")
    private LocalDateTime appointmentTime;
    
    @NotNull(message = "Status cannot be null")
    @Range(min = 0, max = 1, message = "Status must be 0 (scheduled) or 1 (completed)")
    private int status;
    
    @NotBlank(message = "Reason for visiting cannot be blank")
    @Size(max = 200, message = "Reason for visiting must be maximum 200 characters")
    private String reasonForVisiting;
    
    @Size(max = 200, message = "Notes must be maximum 200 characters")
    private String notes;
    
    public Appointment() {}
    
    public Appointment(long id, Doctor doctor, Patient patient, LocalDateTime appointmentTime,
                       int status, String reasonForVisiting, String notes) {
        this.id = id;
        this.doctor = doctor;
        this.patient = patient;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.reasonForVisiting = reasonForVisiting;
        this.notes = notes;
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public Doctor getDoctor() {
        return doctor;
    }
    
    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }
    
    public Patient getPatient() {
        return patient;
    }
    
    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    
    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }
    
    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public String getReasonForVisiting() {
        return reasonForVisiting;
    }
    
    public void setReasonForVisiting(String reasonForVisiting) {
        this.reasonForVisiting = reasonForVisiting;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    @Transient
    public LocalDateTime getEndTime() {
        return appointmentTime.plusHours(1);
    }
    
    @Transient
    public LocalDate getAppointmentDateOnly() {
        return appointmentTime.toLocalDate();
    }
    
    @Transient
    public LocalTime getAppointmentTimeOnly() {
        return appointmentTime.toLocalTime();
    }
}
