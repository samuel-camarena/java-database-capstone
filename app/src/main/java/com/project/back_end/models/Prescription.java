package com.project.back_end.models;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
//import org.springframework.data.mongodb.core.mapping.Document;

//@Document(collection = "prescriptions")
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull(message = "Name is required")
    @Size(min = 3, max = 100, message = "Patient name must be between 3 and 100 characters")
    private String patientName;
    @NotNull(message = "Appointment Id is required")
    @OneToOne
    private long appointmentId;
    @NotNull(message = "Medication is required")
    @Size(min = 3, max = 100, message = "Medication must be between 3 and 100 characters")
    private String medication;
    @NotNull(message = "Dosage is required")
    @Size(min = 3, max = 20, message = "Dosage must be between 3 and 20 characters")
    private String dosage;
    @Size(max = 200, message = "Doctor notes must be maximum 200 characters")
    private Object doctorNotes;

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
}
