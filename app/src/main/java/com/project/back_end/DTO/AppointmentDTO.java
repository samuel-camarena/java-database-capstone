package com.project.back_end.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AppointmentDTO {
    
    private Long id;
    private Long doctorId;
    private String doctorName;
    private Long patientId;
    private String patientName;
    private String patientEmail;
    private String patientPhone;
    private String patientAddress;
    private int status; // "Scheduled:0", "Completed:1", or other statuses (e.g., "Canceled") as needed.
    private LocalDateTime appointmentDateAndTime;
    
    public AppointmentDTO(Long id, Long doctorId, String doctorName, Long patientId, String patientName,
                          String patientEmail, String patientPhone, String patientAddress, int status,
                          LocalDateTime appointmentDateTime) {
        this.id = id;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        
        this.patientId = patientId;
        this.patientName = patientName;
        this.patientEmail = patientEmail;
        this.patientPhone = patientPhone;
        this.patientAddress = patientAddress;
        this.status = status;
        this.appointmentDateAndTime = appointmentDateTime;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getDoctorId() {
        return doctorId;
    }
    
    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }
    
    public String getDoctorName() {
        return doctorName;
    }
    
    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
    
    public Long getPatientId() {
        return patientId;
    }
    
    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }
    
    public String getPatientName() {
        return patientName;
    }
    
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
    
    public String getPatientEmail() {
        return patientEmail;
    }
    
    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }
    
    public String getPatientPhone() {
        return patientPhone;
    }
    
    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }
    
    public String getPatientAddress() {
        return patientAddress;
    }
    
    public void setPatientAddress(String patientAddress) {
        this.patientAddress = patientAddress;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public LocalDateTime getAppointmentDateTime() {
        return appointmentDateAndTime;
    }
    
    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) {
        this.appointmentDateAndTime = appointmentDateTime;
    }
    
    /*
        Derived and Extracted utility Methods.
     */
    public LocalDate getAppointmentDate() {
        return appointmentDateAndTime.toLocalDate();
    }
    
    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDateAndTime = LocalDateTime.of(appointmentDate, appointmentDateAndTime.toLocalTime());
    }
    
    public LocalTime getAppointmentTime() {
        return appointmentDateAndTime.toLocalTime();
    }
    
    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentDateAndTime = LocalDateTime.of(appointmentDateAndTime.toLocalDate(), appointmentTime);
    }
    
    public LocalTime getAppointmentEndTime() {
        return appointmentDateAndTime.toLocalTime().plusHours(1);
    }
}
