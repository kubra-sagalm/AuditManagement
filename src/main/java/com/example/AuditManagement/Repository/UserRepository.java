package com.example.AuditManagement.Repository;
import com.example.AuditManagement.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole_Name(String roleName);

    List<User> findByIdInAndRole_Name(Set<Long> ids, String roleName);


}
