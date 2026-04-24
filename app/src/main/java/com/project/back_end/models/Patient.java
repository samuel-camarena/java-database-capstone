package com.project.back_end.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

import static com.project.back_end.config.EntityConstraintsConfig.*;

@Entity
public class Patient {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @NotBlank(message = NAME_NOT_BLANK_MSG)
    @Size(min = NAME_SIZE_MIN, max = NAME_SIZE_MAX,  message = NAME_INVALID_SIZE_MSG)
    private String name;
    
    @NotBlank(message = EMAIL_NOT_BLANK_MSG)
    @Column(unique = true, name = "email")
    @Email(regexp = EMAIL_VALIDATION_REGEX, message = EMAIL_INVALID_REGEX_MSG)
    private String email;
    
    @NotBlank(message = PASSWORD_SIMPLE_NOT_BLANK_MSG)
    @Pattern(regexp = PASSWORD_SIMPLE_VALIDATION_REGEX, message = PASSWORD_SIMPLE_INVALID_PATTERN_MSG)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    
    @NotBlank(message = PHONE_NOT_BLANK_MSG)
    @Pattern(regexp = PHONE_VALIDATION_REGEX, message = PHONE_INVALID_PATTERN_MSG)
    private String phone;
    
    @NotBlank(message = ADDRESS_NOT_BLANK_MSG)
    @Size(min = ADDRESS_SIZE_MIN, max = ADDRESS_SIZE_MAX, message = ADDRESS_INVALID_SIZE_MSG)
    private String address;
    
    @NotNull(message = DATE_OF_BIRTH_NOT_NULL_MSG)
    @Past(message = DATE_OF_BIRTH_NOT_PAST_MSG)
    private LocalDate dateOfBirth;
    
    @NotBlank(message = EMERGENCY_CONTACT_NOT_BLANK_MSG)
    @Pattern(regexp = PHONE_VALIDATION_REGEX, message = PHONE_INVALID_PATTERN_MSG)
    private String emergencyContact;
    
    @NotBlank(message = INSURANCE_PROVIDER_NOT_BLANK_MSG)
    @Size(min = INSURANCE_PROVIDER_SIZE_MIN, max = INSURANCE_PROVIDER_SIZE_MAX,  message = INSURANCE_PROVIDER_INVALID_SIZE)
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
    
    public Patient(Builder builder) {
        setName(builder.name);
        setEmail(builder.email);
        setPassword(builder.password);
        setPhone(builder.phone);
        setAddress(builder.address);
        setDateOfBirth(builder.dateOfBirth);
        setEmergencyContact(builder.emergencyContact);
        setInsuranceProvider(builder.insuranceProvider);
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
    
    public static class Builder {
        private String name;
        private String email;
        private String password;
        private String phone;
        private String address;
        private LocalDate dateOfBirth;
        private String emergencyContact;
        private String insuranceProvider;
        
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder email(String email) {
            this.email = email;
            return this;
        }
        
        public Builder password(String password) {
            this.password = password;
            return this;
        }
        
        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }
        
        public Builder address(String address) {
            this.address = address;
            return this;
        }
        
        public Builder dateOfBirth(LocalDate dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }
        
        public Builder emergencyContact(String emergencyContact) {
            this.emergencyContact = emergencyContact;
            return this;
        }
        
        public Builder insuranceProvider(String insuranceProvider) {
            this.insuranceProvider = insuranceProvider;
            return this;
        }
        
        public Patient build() {
            return new Patient(this);
        }
    }
}
