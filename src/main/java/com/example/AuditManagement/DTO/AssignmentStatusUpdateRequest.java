package com.example.AuditManagement.DTO;

import com.example.AuditManagement.Entity.Task;
import lombok.Data;

@Data
public class AssignmentStatusUpdateRequest {
    private Task.TaskStatus status;
}