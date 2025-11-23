package com.example.AuditManagement.DTO;

import lombok.Data;

@Data
public class FileResponse {
    private Long id;
    private String fileName;
    private String fileType;
    private Long folderId;
}

