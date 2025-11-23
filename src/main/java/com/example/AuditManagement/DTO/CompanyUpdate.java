package com.example.AuditManagement.DTO;

import lombok.Data;

@Data
public class CompanyUpdate {

    private String name;
    private String contactPerson;
    private String email;
    private String phone;
    private String address;
    private String notes;
}
