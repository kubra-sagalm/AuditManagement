package com.example.AuditManagement.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChecklistItemStatusResponse {
    private String itemKey;
    private boolean completed;
    private LocalDateTime completedAt;
}
