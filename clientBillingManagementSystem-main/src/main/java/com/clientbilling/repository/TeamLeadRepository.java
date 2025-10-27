package com.clientbilling.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.clientbilling.model.TeamLead;

public interface TeamLeadRepository extends JpaRepository<TeamLead, Long> {
  
	Optional<TeamLead> findByteamleadIdNo(String empIdNo);
    Optional<TeamLead> findByUsername(String username);
	Optional<TeamLead> findByEmail(String email);
}