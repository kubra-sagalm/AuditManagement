package com.example.AuditManagement.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CompanyCreate {


    @NotBlank
    private String name;

    @NotBlank
    private String contactPerson;

    @Email
    private String email;

    private String phone;

    private String address;

    private String notes;

}
