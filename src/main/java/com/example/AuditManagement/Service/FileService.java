package com.example.AuditManagement.Service;


import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import com.example.AuditManagement.Entity.FileEntity;
import com.example.AuditManagement.Entity.Folder;
import com.example.AuditManagement.Repository.FileRepository;
import com.example.AuditManagement.Repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FolderRepository folderRepository;
    private final FileRepository fileRepository;

    public FileEntity upload(MultipartFile file, Long folderId) throws Exception {

        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Folder bulunamadı"));

        Long companyId = folder.getCompany().getId();

        String uploadDir = "storage/company/" + companyId + "/" + folderId;

        Files.createDirectories(Paths.get(uploadDir));

        Path filePath = Paths.get(uploadDir, file.getOriginalFilename());

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        FileEntity entity = new FileEntity();
        entity.setFileName(file.getOriginalFilename());
        entity.setFileType(file.getContentType());
        entity.setFileSize(file.getSize());
        entity.setFilePath(filePath.toString());
        entity.setFolder(folder);

        return fileRepository.save(entity);
    }

    public ResponseEntity<Resource> download(Long fileId) throws Exception {

        FileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("Dosya bulunamadı"));

        Path path = Paths.get(file.getFilePath());
        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getFileName() + "\"")
                .body(resource);
    }

    public ResponseEntity<Resource> view(Long fileId) throws Exception {

        FileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("Dosya bulunamadı"));

        Path path = Paths.get(file.getFilePath());
        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getFileType()))
                .body(resource);
    }
}
