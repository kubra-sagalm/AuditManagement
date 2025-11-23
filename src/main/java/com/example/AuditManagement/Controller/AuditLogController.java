package com.example.AuditManagement.Controller;

import com.example.AuditManagement.DTO.AuditLogResponse;
import com.example.AuditManagement.Service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    // Kullanıcı: sadece kendi loglarını görür
    @GetMapping("/me")
    public ResponseEntity<Page<AuditLogResponse>> getMyLogs(
            @AuthenticationPrincipal UserDetails currentUser,
            Pageable pageable
    ) {
        Page<AuditLogResponse> logs =
                auditLogService.getMyLogs(currentUser.getUsername(), pageable);

        return ResponseEntity.ok(logs);
    }

    // Admin: tüm logları görür
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AuditLogResponse>> getAllLogs(Pageable pageable) {

        Page<AuditLogResponse> logs = auditLogService.getAllLogs(pageable);
        return ResponseEntity.ok(logs);
    }
}
