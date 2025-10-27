package com.clientbilling.repository;

import com.clientbilling.model.Admin;
import com.clientbilling.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken,Long> {
    Optional<PasswordResetToken> findByTokenHash(String tokenHash);
    // AdminRepository.java / ClientRepository.java / TeamLeadRepository.java / EmployeeRepository.java
//    Optional<Admin> findByUsername(String username);
//    Optional<Admin> findByEmail(String email);



}
