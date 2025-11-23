package com.example.AuditManagement.Repository;

import com.example.AuditManagement.Entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company,Long> {

    List<Company> findByStatus(Company.CompanyStatus status);

    List<Company> findByTeamMembers_Id(Long userId);

    Optional<Company> findByIdAndStatus(Long id, Company.CompanyStatus status);


}
