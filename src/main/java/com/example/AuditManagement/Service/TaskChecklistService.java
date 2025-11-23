package com.example.AuditManagement.Service;
import com.example.AuditManagement.DTO.ChecklistItemStatusResponse;
import com.example.AuditManagement.DTO.ChecklistItemStatusUpdateRequest;
import com.example.AuditManagement.Entity.Task;
import com.example.AuditManagement.Entity.TaskAssignment;
import com.example.AuditManagement.Entity.TaskChecklistItem;
import com.example.AuditManagement.Repository.TaskChecklistItemRepository;
import com.example.AuditManagement.Repository.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskChecklistService {

    private final TaskRepository taskRepository;
    private final TaskChecklistItemRepository checklistItemRepository;
    private final TaskChecklistItemRepository taskChecklistItemRepository;



    @Transactional
    public List<ChecklistItemStatusResponse> getTaskChecklistForAdmin(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task bulunamadı: " + taskId));

        List<TaskChecklistItem> items = checklistItemRepository.findByTask_Id(task.getId());
        return items.stream().map(this::toDto).toList();
    }


    @Transactional
    public List<ChecklistItemStatusResponse> getTaskChecklistForMember(Long taskId, Long memberId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task bulunamadı: " + taskId));

        boolean assigned = task.getAssignments().stream()
                .map(TaskAssignment::getUser)
                .anyMatch(u -> u.getId().equals(memberId));

        if (!assigned) {
            throw new RuntimeException("Bu task bu kullanıcıya atanmış değil");
        }

        List<TaskChecklistItem> items = checklistItemRepository.findByTask_Id(task.getId());
        return items.stream().map(this::toDto).toList();
    }


    @Transactional
    public ChecklistItemStatusResponse updateItemStatus(Long taskId,
                                                        Long currentUserId,
                                                        boolean isAdmin,
                                                        ChecklistItemStatusUpdateRequest request) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task bulunamadı: " + taskId));


        if (!isAdmin) {
            boolean assigned = task.getAssignments().stream()
                    .map(TaskAssignment::getUser)
                    .anyMatch(u -> u.getId().equals(currentUserId));

            if (!assigned) {
                throw new RuntimeException("Bu task bu kullanıcıya atanmış değil, checklist güncelleyemez");
            }
        }

        String itemKey = request.getItemKey();
        boolean completed = request.getCompleted();

        TaskChecklistItem entity = checklistItemRepository
                .findByTask_IdAndItemKey(taskId, itemKey)
                .orElseGet(() -> {
                    TaskChecklistItem tci = new TaskChecklistItem();
                    tci.setTask(task);
                    tci.setItemKey(itemKey);
                    return tci;
                });

        entity.setCompleted(completed);

        if (completed) {
            if (entity.getCompletedAt() == null) {
                entity.setCompletedAt(LocalDateTime.now());
            }
        } else {
            entity.setCompletedAt(null);
        }

        TaskChecklistItem saved = checklistItemRepository.save(entity);
        return toDto(saved);
    }

    // helper
    private ChecklistItemStatusResponse toDto(TaskChecklistItem entity) {
        ChecklistItemStatusResponse dto = new ChecklistItemStatusResponse();
        dto.setItemKey(entity.getItemKey());
        dto.setCompleted(entity.isCompleted());
        dto.setCompletedAt(entity.getCompletedAt());
        return dto;
    }


    @Transactional
    public void createChecklistForTask(Task task) {


        String[] KEYS = {
                "FIRE_EXTINGUISHER",
                "EMERGENCY_EXIT",
                "FIRST_AID_KIT",

        };

        List<TaskChecklistItem> items = new ArrayList<>();

        for (String key : KEYS) {
            TaskChecklistItem item = new TaskChecklistItem();
            item.setTask(task);
            item.setItemKey(key);
            item.setCompleted(false);
            item.setCompletedAt(null);

            items.add(item);
        }

        taskChecklistItemRepository.saveAll(items);
    }


}
