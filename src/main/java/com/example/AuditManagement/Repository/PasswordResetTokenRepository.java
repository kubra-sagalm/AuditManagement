package com.example.AuditManagement.Repository;

import com.example.AuditManagement.Entity.PasswordResetToken;
import com.example.AuditManagement.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByUser(User user);
}

