package com.example.AuditManagement.Service;

import com.example.AuditManagement.DTO.TaskAssigneeResponse;
import com.example.AuditManagement.DTO.TaskAssignmentResponse;
import com.example.AuditManagement.DTO.TaskCreateRequest;
import com.example.AuditManagement.DTO.TaskResponse;
import com.example.AuditManagement.Entity.*;
import com.example.AuditManagement.Repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final TaskChecklistService taskChecklistService;


    @Transactional
    public TaskResponse createTask(TaskCreateRequest request, Long adminId) {


        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new RuntimeException(
                        "Bu id ile şirket bulunamadı. companyId = " + request.getCompanyId()
                ));


        if (company.getStatus() != Company.CompanyStatus.ACTIVE) {
            throw new RuntimeException(
                    "Şirket var ama ACTIVE değil. companyId = " + company.getId()
                            + ", status = " + company.getStatus()
            );
        }


        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin bulunamadı. adminId = " + adminId));


        if (!"ADMIN".equalsIgnoreCase(admin.getRole().getName())) {
            throw new RuntimeException("Bu işlemi sadece ADMIN yapabilir");
        }


        List<User> assignees = userRepository.findByIdInAndRole_Name(
                request.getAssigneeIds(),
                "TEAM_MEMBER"
        );

        if (assignees.isEmpty()) {
            throw new RuntimeException("En az bir geçerli TEAM_MEMBER seçmelisiniz");
        }


        Task task = new Task();
        task.setCompany(company);
        task.setCreatedBy(admin);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStartDate(request.getStartDate());
        task.setDueDate(request.getDueDate());
        task.setStatus(Task.TaskStatus.PENDING);


        assignees.forEach(user -> {
            TaskAssignment assignment = new TaskAssignment();
            assignment.setUser(user);
            assignment.setStatus(Task.TaskStatus.PENDING);
            task.addAssignment(assignment);
        });

        Task saved = taskRepository.save(task);

        taskChecklistService.createChecklistForTask(saved);


        TaskResponse dto = modelMapper.map(saved, TaskResponse.class);
        dto.setCompanyId(saved.getCompany().getId());
        dto.setCreatedById(saved.getCreatedBy().getId());

        return dto;
    }



    @Transactional
    public List<TaskAssignmentResponse> getMyAssignments(Long memberId) {

        List<TaskAssignment> assignments =
                taskAssignmentRepository.findByUser_Id(memberId);

        return assignments.stream()
                .map(this::mapToAssignmentResponse)
                .toList();
    }



    @Transactional
    public TaskAssignmentResponse updateAssignmentStatus(Long assignmentId,
                                                         Long memberId,
                                                         Task.TaskStatus newStatus) {

        TaskAssignment assignment = taskAssignmentRepository
                .findByIdAndUser_Id(assignmentId, memberId)
                .orElseThrow(() -> new RuntimeException("Görev bulunamadı veya bu kullanıcıya ait değil"));

        if (newStatus == Task.TaskStatus.IN_PROGRESS && assignment.getStartedAt() == null) {
            assignment.setStartedAt(LocalDateTime.now());
        }
        if (newStatus == Task.TaskStatus.COMPLETED && assignment.getCompletedAt() == null) {
            assignment.setCompletedAt(LocalDateTime.now());
        }

        assignment.setStatus(newStatus);

        Task task = assignment.getTask();

        boolean allCompleted = task.getAssignments().stream()
                .allMatch(a -> a.getStatus() == Task.TaskStatus.COMPLETED);

        boolean anyInProgress = task.getAssignments().stream()
                .anyMatch(a -> a.getStatus() == Task.TaskStatus.IN_PROGRESS);

        if (allCompleted) {
            task.setStatus(Task.TaskStatus.COMPLETED);
            task.setCompletedAt(LocalDateTime.now());
        } else if (anyInProgress) {
            task.setStatus(Task.TaskStatus.IN_PROGRESS);
        } else {
            task.setStatus(Task.TaskStatus.PENDING);
            task.setCompletedAt(null);
        }

        return mapToAssignmentResponse(assignment);
    }



    @Transactional
    public void deleteTask(Long taskId, Long adminId) {

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin kullanıcı bulunamadı"));

        if (!"ADMIN".equalsIgnoreCase(admin.getRole().getName())) {
            throw new RuntimeException("Bu işlemi sadece ADMIN yapabilir");
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task bulunamadı"));

        if (task.getStatus() != Task.TaskStatus.PENDING) {
            throw new RuntimeException("Sadece PENDING durumundaki task silinebilir");
        }

        taskRepository.delete(task);
    }




    private TaskAssignmentResponse mapToAssignmentResponse(TaskAssignment assignment) {
        TaskAssignmentResponse dto = new TaskAssignmentResponse();
        dto.setAssignmentId(assignment.getId());
        dto.setTaskId(assignment.getTask().getId());
        dto.setTaskTitle(assignment.getTask().getTitle());
        dto.setStatus(assignment.getStatus());
        dto.setStartedAt(assignment.getStartedAt());
        dto.setCompletedAt(assignment.getCompletedAt());
        dto.setUserId(assignment.getUser().getId());
        dto.setUserEmail(assignment.getUser().getEmail());
        return dto;
    }



    public List<TaskAssigneeResponse> getAssignableTeamMembers() {

        List<User> members = userRepository.findByRole_Name("TEAM_MEMBER");

        if (members.isEmpty()) {
            throw new RuntimeException("Atanabilir TEAM_MEMBER bulunamadı");
        }

        return members.stream()
                .map(user -> {
                    TaskAssigneeResponse dto = new TaskAssigneeResponse();
                    dto.setId(user.getId());
                    dto.setEmail(user.getEmail());
                    return dto;
                })
                .toList();
    }




    public List<TaskResponse> getTasksCreatedByAdmin(Long adminId) {
        List<Task> tasks = taskRepository.findByCreatedBy_Id(adminId);

        return tasks.stream()
                .map(task -> {
                    TaskResponse dto = modelMapper.map(task, TaskResponse.class);
                    // createTask'te yaptığın gibi ekstra alanları set edelim:
                    dto.setCompanyId(task.getCompany().getId());
                    dto.setCreatedById(task.getCreatedBy().getId());
                    return dto;
                })
                .toList();
    }
}
