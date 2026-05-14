package com.project.back_end.DTO;

public class LoginDTO {
    
    private String identifier; // email para Doctor/Paciente, username para Administrador
    private String password;
    private String role;
    
    public LoginDTO(String identifier, String password, String role) {
        this.identifier = identifier;
        this.password = password;
        this.role = role;
    }
    
    public String getIdentifier() {
        return identifier;
    }
    
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
}
