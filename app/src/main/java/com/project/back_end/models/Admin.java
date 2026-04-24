package com.project.back_end.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.back_end.utils.AppHelper;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.beans.factory.annotation.Value;

import static com.project.back_end.config.EntityConstraintsConfig.*;

@Entity
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @NotNull(message = USERNAME_NOT_NULL_MSG)
    @Size(min = USERNAME_SIZE_MIN, max = USERNAME_SIZE_MAX, message = USERNAME_INVALID_SIZE_MSG)
    private String username;
    
    @NotNull(message = PASSWORD_SIMPLE_NOT_BLANK_MSG)
    @Pattern(regexp = PASSWORD_SIMPLE_VALIDATION_REGEX, message = PASSWORD_SIMPLE_INVALID_PATTERN_MSG)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    
    @NotBlank(message = EMAIL_NOT_BLANK_MSG)
    @Column(unique = true, name = "email")
    @Email(regexp = EMAIL_VALIDATION_REGEX, message = EMAIL_INVALID_REGEX_MSG)
    private String email;
    
    public Admin() {
    }
    
    public Admin(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
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
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
}
