package com.project.back_end.DTO;

import java.util.List;

public class DoctorDTO {
    private long id;
    private String name;
    private String email;
    private String password;
    private String specialty;
    private String phone;
    private List<String> availableTimes;
    private int yearsOfExperience;
    private String clinicAddress;
    private double rating;
    
    public DoctorDTO(long id, String name, String email, String password, String specialty, String phone,
                     List<String> availableTimes, int yearsOfExperience, String clinicAddress, double rating) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        setSpecialtyCapitalized(specialty);
        this.phone = phone;
        this.availableTimes = availableTimes;
        this.yearsOfExperience = yearsOfExperience;
        this.clinicAddress = clinicAddress;
        this.rating = rating;
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
    
    public String getSpecialty() {
        return specialty;
    }
    
    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }
    // TODO: Compare and set a Specialty from a Enum
    public void setSpecialtyCapitalized(String specialty) {
        this.specialty = specialty.substring(0,1).toUpperCase() + specialty.substring(1);
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public List<String> getAvailableTimes() {
        return availableTimes;
    }
    
    public void setAvailableTimes(List<String> availableTimes) {
        this.availableTimes = availableTimes;
    }
    
    public int getYearsOfExperience() {
        return yearsOfExperience;
    }
    
    public void setYearsOfExperience(int yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }
    
    public String getClinicAddress() {
        return clinicAddress;
    }
    
    public void setClinicAddress(String clinicAddress) {
        this.clinicAddress = clinicAddress;
    }
    
    public double getRating() {
        return rating;
    }
    
    public void setRating(double rating) {
        this.rating = rating;
    }
}
