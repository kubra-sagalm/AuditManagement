package com.example.AuditManagement.DTO;


import com.example.AuditManagement.Entity.Task;
import com.example.AuditManagement.Entity.Task.TaskStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class TaskResponse {

    private Long id;
    private Long companyId;
    private String title;
    private String description;
    private Task.TaskStatus status;
    private LocalDate startDate;
    private LocalDate dueDate;
    private LocalDateTime completedAt;
    private Long createdById;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<TaskAssignmentResponse> assignments;
}
