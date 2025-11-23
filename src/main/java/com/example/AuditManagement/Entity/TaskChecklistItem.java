package com.example.AuditManagement.Entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Entity
@Table(
        name = "task_checklist_items",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"task_id", "item_key"})
        }
)
@Getter
@Setter
public class TaskChecklistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;


    @Column(name = "item_key", nullable = false, length = 100)
    private String itemKey;

    @Column(name = "completed", nullable = false)
    private boolean completed = false;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

}
