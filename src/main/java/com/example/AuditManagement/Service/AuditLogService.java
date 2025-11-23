package com.example.AuditManagement.Service;

import com.example.AuditManagement.DTO.AuditLogResponse;
import com.example.AuditManagement.Entity.AuditLog;
import com.example.AuditManagement.Entity.User;
import com.example.AuditManagement.Repository.AuditLogRepository;
import com.example.AuditManagement.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    // 🔹 Her yerden çağıracağın ana log metodu
    @Transactional
    public void log(String userEmail,
                    String action,
                    String entityType,
                    Long entityId,
                    String description,
                    String ipAddress,
                    String userAgent) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found for logging"));

        AuditLog log = new AuditLog();
        log.setUser(user);
        log.setRoleAtTime(user.getRole());  // snapshot
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDescription(description);
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);

        auditLogRepository.save(log);
    }


    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getMyLogs(String userEmail, Pageable pageable) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Page<AuditLog> page = auditLogRepository.findByUserOrderByCreatedAtDesc(user, pageable);

        return page.map(this::toDto);
    }


    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getAllLogs(Pageable pageable) {

        Page<AuditLog> page = auditLogRepository.findAllByOrderByCreatedAtDesc(pageable);

        return page.map(this::toDto);
    }


    private AuditLogResponse toDto(AuditLog log) {
        return new AuditLogResponse(
                log.getId(),
                log.getUser().getFullName(),
                log.getUser().getEmail(),
                log.getRoleAtTime() != null ? log.getRoleAtTime().getName() : null,
                log.getAction(),
                log.getEntityType(),
                log.getEntityId(),
                log.getDescription(),
                log.getIpAddress(),
                log.getUserAgent(),
                log.getCreatedAt()
        );
    }
}
