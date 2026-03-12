package com.project.back_end.models;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "prescriptions")
public class Prescription {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @NotNull(message = "Name cannot be null")
    @Size(min = 3, max = 100, message = "Patient name must be between 3 and 100 characters")
    private String patientName;
    
    @NotNull(message = "Appointment Id cannot be null")
    @OneToOne
    private long appointmentId;
    
    @NotNull(message = "Medication cannot be null")
    @Size(min = 3, max = 100, message = "Medication must be between 3 and 100 characters")
    private String medication;
    
    @NotNull(message = "Dosage cannot be null")
    @Size(min = 3, max = 20, message = "Dosage must be between 3 and 20 characters")
    private String dosage;
    
    @Size(max = 200, message = "Doctor notes must be maximum 200 characters")
    private String doctorNotes;
    
    public Prescription() {}
    
    public Prescription(long id, String patientName, long appointmentId, String medication, String dosage) {
        this.id = id;
        this.patientName = patientName;
        this.appointmentId = appointmentId;
        this.medication = medication;
        this.dosage = dosage;
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getPatientName() {
        return patientName;
    }
    
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
    
    public long getAppointmentId() {
        return appointmentId;
    }
    
    public void setAppointmentId(long appointmentId) {
        this.appointmentId = appointmentId;
    }
    
    public String getMedication() {
        return medication;
    }
    
    public void setMedication(String medication) {
        this.medication = medication;
    }
    
    public String getDosage() {
        return dosage;
    }
    
    public void setDosage(String dosage) {
        this.dosage = dosage;
    }
    
    public String getDoctorNotes() {
        return doctorNotes;
    }
    
    public void setDoctorNotes(String doctorNotes) {
        this.doctorNotes = doctorNotes;
    }
}

