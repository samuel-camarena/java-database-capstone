package com.project.back_end.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.back_end.utils.ApplicationHelper;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.UniqueElements;

import java.time.LocalDate;

@Entity
public class Patient {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @NotNull(message = "Name cannot be null")
    @Size(min = 3, max = 100,  message = "Name must be between 3 and 100 characters")
    private String name;
    
    @NotNull(message = "Email cannot be null")
    @UniqueElements
    @Email(regexp = ApplicationHelper.EMAIL_VALIDATION_REGEX,
        message = "Email address must have a valid format")
    private String email;
    
    @NotNull(message = "Password cannot be null")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    @Pattern(regexp = ApplicationHelper.PASSWORD_SIMPLE_VALIDATION_REGEX, message = "Password must contain " +
        "any combination of letters a-z, A-Z and/or numbers 0-9, with length between 6 and 20")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    
    @NotNull(message = "Phone number cannot be null")
    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
    private String phone;
    
    @NotNull(message = "Address cannot be null")
    @Size(max = 255, message = "Address must be maximum 255 characters")
    private String address;
    
    @NotNull(message = "Date of birth cannot be null")
    @Past(message = "Date of birth must be in past")
    private LocalDate dateOfBirth;
    
    @NotNull(message = "Emergency contact phone number cannot be null")
    @Pattern(regexp = "\\d{10}", message = "Emergency contact phone number must be 10 digits")
    private String emergencyContact;
    
    @Size(min = 3, max = 100,  message = "Name must be between 3 and 100 characters")
    private String insuranceProvider;
    
    public Patient() {}
    
    public Patient(String name, String email, String password, String phone, String address,
                   LocalDate dateOfBirth, String emergencyContact, String insuranceProvider) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.emergencyContact = emergencyContact;
        this.insuranceProvider = insuranceProvider;
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public String getEmergencyContact() {
        return emergencyContact;
    }
    
    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }
    
    public String getInsuranceProvider() {
        return insuranceProvider;
    }
    
    public void setInsuranceProvider(String insuranceProvider) {
        this.insuranceProvider = insuranceProvider;
    }
}
