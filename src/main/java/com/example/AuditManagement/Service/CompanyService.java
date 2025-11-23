package com.example.AuditManagement.Service;

import com.example.AuditManagement.DTO.CompanyCreate;
import com.example.AuditManagement.DTO.CompanyResponse;
import com.example.AuditManagement.DTO.CompanyUpdate;
import com.example.AuditManagement.Entity.Company;
import com.example.AuditManagement.Entity.User;
import com.example.AuditManagement.Repository.CompanyRepository;
import com.example.AuditManagement.Repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {

    @Autowired
    public CompanyRepository companyRepository;

    @Autowired
    public ModelMapper modelMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuditLogService auditLogService;


    public CompanyResponse createCompany(CompanyCreate request, HttpServletRequest httpRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User creator = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + email));


        Company company = modelMapper.map(request, Company.class);
        company.setCreatedBy(creator);

        Company saved = companyRepository.save(company);


        String ip = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");

        auditLogService.log(
                email,
                "COMPANY_CREATED",
                "COMPANY",
                saved.getId(),
                "Yeni şirket oluşturuldu: " + saved.getName(),
                ip,
                userAgent
        );

        return modelMapper.map(saved, CompanyResponse.class);
    }

    public List<CompanyResponse> getAllCompanies() {
        return companyRepository.findAll()
                .stream()
                .map(company -> modelMapper.map(company, CompanyResponse.class))
                .toList();
    }


    public List<CompanyResponse> getCompaniesByStatus(String status) {

        if (status == null) {
            throw new IllegalArgumentException("Status null olamaz");
        }

        Company.CompanyStatus enumStatus;
        try {
            enumStatus = Company.CompanyStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status sadece ACTIVE veya PASSIVE olabilir");
        }

        return companyRepository.findByStatus(enumStatus)
                .stream()
                .map(c -> modelMapper.map(c, CompanyResponse.class))
                .toList();
    }

    public CompanyResponse updateCompanyStatus(Long companyId, Company.CompanyStatus status) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found with id: " + companyId));

        company.setStatus(status);
        companyRepository.save(company);

        return modelMapper.map(company, CompanyResponse.class);
    }



    public CompanyResponse updateCompany(Long id, CompanyUpdate request) {

        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found with id: " + id));

        company.setName(request.getName());
        company.setContactPerson(request.getContactPerson());
        company.setEmail(request.getEmail());
        company.setPhone(request.getPhone());
        company.setAddress(request.getAddress());
        company.setNotes(request.getNotes());

        Company saved = companyRepository.save(company);

        return modelMapper.map(saved, CompanyResponse.class);
    }

    public void deleteCompany(Long id) {

        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found with id: " + id));

        companyRepository.delete(company);
    }


















}
