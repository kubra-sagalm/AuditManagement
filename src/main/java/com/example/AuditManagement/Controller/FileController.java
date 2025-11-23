package com.example.AuditManagement.Controller;

import com.example.AuditManagement.DTO.FileResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.core.io.Resource;


import com.example.AuditManagement.Entity.FileEntity;
import com.example.AuditManagement.Service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")

public class FileController {

    private final FileService fileService;

    @PreAuthorize("hasAnyRole('ADMIN','TEAM_MEMBER')")
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public FileResponse upload(@RequestParam("file") MultipartFile file,
                               @RequestParam("folderId") Long folderId) throws Exception {

        FileEntity savedFile = fileService.upload(file, folderId);

        return mapToDto(savedFile);
    }

    private FileResponse mapToDto(FileEntity file) {
        FileResponse dto = new FileResponse();
        dto.setId(file.getId());
        dto.setFileName(file.getFileName());
        dto.setFileType(file.getFileType());
        dto.setFolderId(file.getFolder().getId());
        return dto;
    }


    @PreAuthorize("hasAnyRole('ADMIN','TEAM_MEMBER')")
    @GetMapping("/{fileId}/download")
    public ResponseEntity<Resource> download(@PathVariable Long fileId) throws Exception {
        return fileService.download(fileId);
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEAM_MEMBER')")
    @GetMapping("/{fileId}/view")
    public ResponseEntity<Resource> view(@PathVariable Long fileId) throws Exception {
        return fileService.view(fileId);
    }
}
