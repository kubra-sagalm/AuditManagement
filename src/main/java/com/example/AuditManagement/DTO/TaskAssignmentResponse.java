package com.example.AuditManagement.DTO;

import com.example.AuditManagement.Entity.Task;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskAssignmentResponse {

    private Long assignmentId;
    private Long taskId;
    private String taskTitle;
    private Task.TaskStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    private Long userId;
    private String userEmail; // istersen
}