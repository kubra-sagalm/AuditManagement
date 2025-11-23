package com.example.AuditManagement.Service;

import com.example.AuditManagement.Entity.Company;
import com.example.AuditManagement.Entity.Folder;
import com.example.AuditManagement.Repository.CompanyRepository;
import com.example.AuditManagement.Repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;
    private final CompanyRepository companyRepository;

    public Folder createFolder(Long companyId, String name, Long parentId) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Şirket bulunamadı"));

        Folder folder = new Folder();
        folder.setName(name);
        folder.setCompany(company);

        if (parentId != null) {
            Folder parent = folderRepository.findById(parentId)
                    .orElseThrow(() -> new RuntimeException("Parent folder bulunamadı"));
            folder.setParent(parent);
        }

        return folderRepository.save(folder);
    }

    public List<Folder> getRootFolders(Long companyId) {
        return folderRepository.findByCompanyIdAndParentIsNull(companyId);
    }

    public List<Folder> getChildren(Long parentId) {
        return folderRepository.findByParentId(parentId);
    }
}
