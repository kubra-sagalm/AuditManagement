package com.example.AuditManagement.DTO;

import lombok.Data;

@Data
public class UpdateUserRequest {

    private String fullName;
    private String email;
    private String phone;

}
