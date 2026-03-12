package com.project.back_end.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
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
    private int status;
    
    public Appointment() {}
    
    public Appointment(long id, Doctor doctor, Patient patient, LocalDateTime appointmentTime, int status) {
        this.id = id;
        this.doctor = doctor;
        this.patient = patient;
        this.appointmentTime = appointmentTime;
        this.status = status;
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
