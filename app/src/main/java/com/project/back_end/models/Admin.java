package com.project.back_end.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.back_end.utils.ApplicationHelper;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
public class Admin {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @NotNull(message = "Username cannot be null")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @NotNull(message = "Password cannot be null")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    @Pattern(regexp = ApplicationHelper.PASSWORD_SIMPLE_VALIDATION_REGEX, message = "Password must contain " +
        "any combination of letters a-z, A-Z and/or numbers 0-9, with length between 6 and 20")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    
    public Admin() {
    }
    
    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
