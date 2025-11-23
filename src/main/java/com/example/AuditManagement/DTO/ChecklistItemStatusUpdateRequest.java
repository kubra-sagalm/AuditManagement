package com.example.AuditManagement.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChecklistItemStatusUpdateRequest {

    @NotBlank
    private String itemKey;

    @NotNull
    private Boolean completed;
}
