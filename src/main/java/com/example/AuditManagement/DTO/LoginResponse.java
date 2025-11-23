package com.example.AuditManagement.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
@Data
@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String role;
    private String fullName;
}
