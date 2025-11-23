package com.example.AuditManagement.Controller;

import com.example.AuditManagement.DTO.FolderCreateRequest;
import com.example.AuditManagement.DTO.FolderResponse;
import com.example.AuditManagement.Entity.Folder;
import com.example.AuditManagement.Service.FolderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/folders")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class FolderController {

    @Autowired
    public FolderService folderService;

    @PreAuthorize("hasAnyRole('ADMIN','TEAM_MEMBER')")
    @PostMapping
    public FolderResponse create(@RequestBody FolderCreateRequest request) {
        Folder folder = folderService.createFolder(
                request.getCompanyId(),
                request.getName(),
                request.getParentId()
        );
        return mapToDto(folder);
    }

    private FolderResponse mapToDto(Folder folder) {
        FolderResponse dto = new FolderResponse();
        dto.setId(folder.getId());
        dto.setName(folder.getName());
        dto.setParentId(folder.getParent() != null ? folder.getParent().getId() : null);
        return dto;
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEAM_MEMBER')")
    @GetMapping("/company/{companyId}")
    public List<FolderResponse> getRootFolders(@PathVariable Long companyId) {
        return folderService.getRootFolders(companyId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEAM_MEMBER')")
    @GetMapping("/{folderId}/children")
    public List<FolderResponse> getChildren(@PathVariable Long folderId) {
        return folderService.getChildren(folderId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }
}
