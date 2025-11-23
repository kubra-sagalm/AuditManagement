package com.example.AuditManagement.Repository;

import com.example.AuditManagement.Entity.TaskChecklistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskChecklistItemRepository extends JpaRepository<TaskChecklistItem, Long> {

    List<TaskChecklistItem> findByTask_Id(Long taskId);

    Optional<TaskChecklistItem> findByTask_IdAndItemKey(Long taskId, String itemKey);
}
