package com.example.AuditManagement.Controller;

import com.example.AuditManagement.DTO.CompanyCreate;
import com.example.AuditManagement.DTO.CompanyResponse;
import com.example.AuditManagement.DTO.CompanyUpdate;
import com.example.AuditManagement.Entity.Company;
import com.example.AuditManagement.Service.CompanyService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/company")
@SecurityRequirement(name = "bearerAuth")
public class CompanyController {

    @Autowired
    public CompanyService companyService;

    @GetMapping("/AllList")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CompanyResponse>> getAllCompanies() {
        List<CompanyResponse> companies = companyService.getAllCompanies();
        return ResponseEntity.ok(companies);
    }

    @PostMapping("/Add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompanyResponse> createCompany(@RequestBody @Valid CompanyCreate request, HttpServletRequest httpRequest) {
        CompanyResponse response = companyService.createCompany(request,httpRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/StatusList")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CompanyResponse>> getCompanies(
            @RequestParam(required = false) String status) {

        List<CompanyResponse> companies;

        if (status == null) {
            companies = companyService.getAllCompanies();
        } else {
            companies = companyService.getCompaniesByStatus(status);
        }

        return ResponseEntity.ok(companies);
    }


    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompanyResponse> updateCompanyStatus(
            @PathVariable Long id,
            @RequestParam Company.CompanyStatus status // <-- BURASI ENUM OLACAK
    ) {
        CompanyResponse response = companyService.updateCompanyStatus(id, status);
        return ResponseEntity.ok(response);
    }



    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompanyResponse> updateCompany(
            @PathVariable Long id,
            @RequestBody CompanyUpdate request) {
        return ResponseEntity.ok(companyService.updateCompany(id, request));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }








}
