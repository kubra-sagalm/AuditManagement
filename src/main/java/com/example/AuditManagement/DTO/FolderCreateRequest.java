package com.example.AuditManagement.DTO;

import lombok.Data;

@Data
public class FolderCreateRequest {
    private Long companyId;
    private String name;
    private Long parentId;
}
