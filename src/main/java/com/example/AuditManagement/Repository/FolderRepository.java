package com.example.AuditManagement.Repository;

import com.example.AuditManagement.Entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FolderRepository extends JpaRepository<Folder, Long> {

    List<Folder> findByCompanyIdAndParentIsNull(Long companyId);

    List<Folder> findByParentId(Long parentId);
}
