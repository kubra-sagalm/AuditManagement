package com.example.AuditManagement.Controller;

import com.example.AuditManagement.DTO.*;
import com.example.AuditManagement.Entity.Task;
import com.example.AuditManagement.Entity.User;
import com.example.AuditManagement.Repository.UserRepository;
import com.example.AuditManagement.Service.TaskChecklistService;
import com.example.AuditManagement.Service.TaskService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/tasks")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    @Autowired
    public TaskService taskService;
    @Autowired
    public UserRepository userRepository;

    @Autowired
    public TaskChecklistService taskChecklistService;


    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Kullanıcı oturum açmamış");
        }

        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + email));

        return user.getId();
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskResponse> createTask(
            @RequestBody @Valid TaskCreateRequest request
    ) {
        Long adminId = getCurrentUserId();
        TaskResponse response = taskService.createTask(request, adminId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        Long adminId = getCurrentUserId();
        taskService.deleteTask(taskId, adminId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/my")
    @PreAuthorize("hasRole('TEAM_MEMBER')")
    public ResponseEntity<List<TaskAssignmentResponse>> getMyAssignments() {
        Long memberId = getCurrentUserId();
        List<TaskAssignmentResponse> list = taskService.getMyAssignments(memberId);
        return ResponseEntity.ok(list);
    }


    @PatchMapping("/assignments/{assignmentId}/status")
    @PreAuthorize("hasRole('TEAM_MEMBER')")
    public ResponseEntity<TaskAssignmentResponse> updateMyAssignmentStatus(
            @PathVariable Long assignmentId,
            @RequestBody @Valid AssignmentStatusUpdateRequest request
    ) {
        Long memberId = getCurrentUserId();
        Task.TaskStatus newStatus = request.getStatus();
        TaskAssignmentResponse response =
                taskService.updateAssignmentStatus(assignmentId, memberId, newStatus);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/assignees/team-members")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TaskAssigneeResponse>> getAssignableTeamMembers() {
        List<TaskAssigneeResponse> list = taskService.getAssignableTeamMembers();
        return ResponseEntity.ok(list);
    }


    @GetMapping("/created-by-me")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TaskResponse>> getTasksCreatedByCurrentAdmin() {
        Long adminId = getCurrentUserId();
        List<TaskResponse> tasks = taskService.getTasksCreatedByAdmin(adminId);
        return ResponseEntity.ok(tasks);
    }


    @GetMapping("/{taskId}/checklist")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ChecklistItemStatusResponse>> getTaskChecklistForAdmin(
            @PathVariable Long taskId
    ) {
        List<ChecklistItemStatusResponse> list =
                taskChecklistService.getTaskChecklistForAdmin(taskId);
        return ResponseEntity.ok(list);
    }


    @GetMapping("/{taskId}/my-checklist")
    @PreAuthorize("hasRole('TEAM_MEMBER')")
    public ResponseEntity<List<ChecklistItemStatusResponse>> getTaskChecklistForMember(
            @PathVariable Long taskId
    ) {
        Long memberId = getCurrentUserId();
        List<ChecklistItemStatusResponse> list =
                taskChecklistService.getTaskChecklistForMember(taskId, memberId);
        return ResponseEntity.ok(list);
    }


    @PatchMapping("/{taskId}/checklist/item")
    @PreAuthorize("hasAnyRole('ADMIN','TEAM_MEMBER')")
    public ResponseEntity<ChecklistItemStatusResponse> updateChecklistItem(
            @PathVariable Long taskId,
            @RequestBody @Valid ChecklistItemStatusUpdateRequest request
    ) {
        Long currentUserId = getCurrentUserId();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN"));

        ChecklistItemStatusResponse dto =
                taskChecklistService.updateItemStatus(taskId, currentUserId, isAdmin, request);

        return ResponseEntity.ok(dto);
    }


}

