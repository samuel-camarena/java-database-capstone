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

import static com.project.back_end.config.EntityConstraintsConfig.*;

@Entity
public class Appointment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @NotNull(message = DOCTOR_NOT_NULL_MSG)
    @ManyToOne
    private Doctor doctor;
    
    @NotNull(message = PATIENT_NOT_NULL_MSG)
    @ManyToOne
    private Patient patient;
    
    @NotNull(message = APPOINTMENT_NOT_NULL_MSG)
    @Future(message = APPOINTMENT_DATE_TIME_AT_FUTURE_MSG)
    private LocalDateTime appointmentTime;
    
    @NotNull(message = STATUS_NOT_NULL_MSG)
    @Range(min = 0, max = 1, message = STATUS_INVALID_RANGE_MSG)
    private int status;
    
    @NotBlank(message = VISITING_REASON_NOT_BLANK_MSG)
    @Size(max = 200, message = VISITING_REASON_INVALID_MAX_MSG)
    private String reasonForVisiting;
    
    @Size(max = 200, message = NOTES_INVALID_MAX_MSG)
    private String notes;
    
    public Appointment() {}
    
    public Appointment(Doctor doctor, Patient patient, LocalDateTime appointmentTime,
                       int status, String reasonForVisiting, String notes) {
        this.doctor = doctor;
        this.patient = patient;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.reasonForVisiting = reasonForVisiting;
        this.notes = notes;
    }
    
    public Appointment(Builder builder) {
        setDoctor(builder.doctor);
        setPatient(builder.patient);
        setAppointmentTime(builder.appointmentTime);
        setStatus(builder.status);
        setReasonForVisiting(builder.reasonForVisiting);
        setNotes(builder.notes);
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
    
    public static class Builder {
        private Doctor doctor;
        private Patient patient;
        private LocalDateTime appointmentTime ;
        private int status;
        private String reasonForVisiting;
        private String notes;
        
        public Builder doctor(Doctor doctor) {
            this.doctor = doctor;
            return this;
        }
        
        public Builder patient(Patient patient) {
            this.patient = patient;
            return this;
        }
        
        public Builder appointmentTime(LocalDateTime appointmentTime) {
            this.appointmentTime = appointmentTime;
            return this;
        }
        
        public Builder status(int status) {
            this.status = status;
            return this;
        }
        
        public Builder reasonForVisiting(String reasonForVisiting) {
            this.reasonForVisiting = reasonForVisiting;
            return this;
        }
        
        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }
        
        public Appointment build() {
            return new Appointment(this);
        }
    }
}
