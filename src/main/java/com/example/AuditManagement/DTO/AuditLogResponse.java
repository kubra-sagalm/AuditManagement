package com.example.AuditManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class AuditLogResponse {

    private Long id;

    private String userFullName;
    private String userEmail;

    private String roleNameAtTime;   // işlem anındaki rol

    private String action;
    private String entityType;
    private Long entityId;

    private String description;

    private String ipAddress;
    private String userAgent;

    private LocalDateTime createdAt;
}
