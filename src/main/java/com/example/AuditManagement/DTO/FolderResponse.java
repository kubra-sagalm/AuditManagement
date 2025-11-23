package com.example.AuditManagement.DTO;

import lombok.Data;

@Data
public class FolderResponse {
    private Long id;
    private String name;
    private Long parentId;
}
