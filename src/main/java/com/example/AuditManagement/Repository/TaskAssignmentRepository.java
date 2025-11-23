package com.example.AuditManagement.Repository;

import com.example.AuditManagement.Entity.TaskAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository

public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, Long> {

    List<TaskAssignment> findByUser_Id(Long userId);

    Optional<TaskAssignment> findByIdAndUser_Id(Long id, Long userId);
}
