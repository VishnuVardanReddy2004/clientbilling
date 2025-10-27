package com.clientbilling.repository;

import com.clientbilling.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUsername(String username); 
    Optional<Admin> findByAdminIdNo(String adminIdNo);// for login/authentication
	Optional<Admin> findByEmail(String email);
}