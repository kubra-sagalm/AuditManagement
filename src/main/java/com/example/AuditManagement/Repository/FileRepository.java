package com.example.AuditManagement.Repository;

import com.example.AuditManagement.Entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<FileEntity, Long> {
    List<FileEntity> findByFolderId(Long folderId);
}
