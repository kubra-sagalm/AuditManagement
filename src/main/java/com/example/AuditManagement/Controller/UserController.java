package com.example.AuditManagement.Controller;

import com.example.AuditManagement.DTO.UpdateUserRequest;
import com.example.AuditManagement.DTO.UserProfileResponse;
import com.example.AuditManagement.Entity.User;
import com.example.AuditManagement.Repository.UserRepository;
import com.example.AuditManagement.Service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    @Autowired
    public UserService userService;


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/team-members")
    public List<User> getAllTeamMembers() {
        return userService.getAllTeamMembers();
    }



    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal UserDetails currentUser,
            @RequestBody UpdateUserRequest request
    ) {
        try {
            UserProfileResponse response =
                    userService.updateCurrentUserProfile(currentUser.getUsername(), request);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            // Örn: Email already in use
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal UserDetails currentUser) {
        try {
            UserProfileResponse response =
                    userService.getCurrentUserProfile(currentUser.getUsername());

            return ResponseEntity.ok(response);

        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }





}
