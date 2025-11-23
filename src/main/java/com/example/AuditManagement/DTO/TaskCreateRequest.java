package com.example.AuditManagement.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class TaskCreateRequest {
    private Long companyId;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate dueDate;
    private Set<Long> assigneeIds; // TEAM_MEMBER id'leri
}