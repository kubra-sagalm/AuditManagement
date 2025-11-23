package com.example.AuditManagement.Controller;


import com.example.AuditManagement.DTO.*;
import com.example.AuditManagement.Entity.PasswordResetToken;
import com.example.AuditManagement.Entity.User;
import com.example.AuditManagement.Entity.Role;
import com.example.AuditManagement.Repository.PasswordResetTokenRepository;
import com.example.AuditManagement.Repository.UserRepository;
import com.example.AuditManagement.Repository.RoleRepository;
import com.example.AuditManagement.Service.AuditLogService;
import com.example.AuditManagement.Service.JwtService;

import com.example.AuditManagement.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserService userService;
    private final AuditLogService auditLogService;
    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UserRepository userRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder,
                          UserDetailsService userDetailsService, PasswordResetTokenRepository passwordResetTokenRepository, UserService userService, AuditLogService auditLogService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userService = userService;
        this.auditLogService = auditLogService;
    }

    // KAYIT OLMA - REGISTER
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request, HttpServletRequest httpRequest) {

        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Email already in use");
        }

        Role defaultRole = roleRepository
                .findByName("TEAM_MEMBER")
                .orElseThrow(() -> new RuntimeException("Default role TEAM_MEMBER not found"));

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(defaultRole);
        user.setActive(true);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        String ip = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");

        auditLogService.log(
                user.getEmail(),          // işlem yapan
                "USER_REGISTERED",        // action
                "USER",                   // entityType
                user.getId(),             // entityId
                "Yeni kullanıcı kayıt oldu: " + user.getFullName(),
                ip,
                userAgent
        );

        var userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtService.generateToken(userDetails);

        return ResponseEntity
                .created(URI.create("/api/users/" + user.getId()))
                .body(new LoginResponse(
                        token,
                        user.getRole().getName(),
                        user.getFullName()
                ));
    }

    //  LOGIN
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        var authToken = new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        );
        authenticationManager.authenticate(authToken);

        var userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtService.generateToken(userDetails);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        LoginResponse response = new LoginResponse(
                token,
                user.getRole().getName(),
                user.getFullName()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Bu email ile kullanıcı bulunamadı"));

        Optional<PasswordResetToken> existingTokenOpt = passwordResetTokenRepository.findByUser(user);

        String newToken = UUID.randomUUID().toString();
        LocalDateTime newExpiry = LocalDateTime.now().plusMinutes(30);

        String resetLink = "http://localhost:3000/reset-password?token=" + newToken;

        if (existingTokenOpt.isPresent()) {

            PasswordResetToken existingToken = existingTokenOpt.get();
            existingToken.setToken(newToken);
            existingToken.setExpiresAt(newExpiry);

            passwordResetTokenRepository.save(existingToken);

            //  Mail gönder
            userService.sendMail(
                    user.getEmail(),
                    "Şifre Sıfırlama Bağlantısı",
                    "Yeni şifre sıfırlama linkiniz: " + resetLink
            );

            return ResponseEntity.ok("Şifre sıfırlama linki mail adresinize gönderildi.");
        }


        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(newToken);
        resetToken.setUser(user);
        resetToken.setExpiresAt(newExpiry);

        passwordResetTokenRepository.save(resetToken);


        userService.sendMail(
                user.getEmail(),
                "Şifre Sıfırlama Bağlantısı",
                "Şifre sıfırlama linkiniz: " + resetLink
        );

        return ResponseEntity.ok("Şifre sıfırlama linki mail adresinize gönderildi.");
    }


    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {

        PasswordResetToken resetToken = passwordResetTokenRepository
                .findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Token geçersiz"));

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token süresi dolmuş");
        }

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);

        return ResponseEntity.ok("Şifre başarıyla güncellendi.");
    }



}
