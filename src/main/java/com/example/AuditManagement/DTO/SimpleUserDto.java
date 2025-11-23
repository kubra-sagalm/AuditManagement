package com.example.AuditManagement.DTO;

import lombok.Data;

@Data
public class SimpleUserDto {
    private Long id;
    private String fullName; // name + surname olabilir
    private String email;
}