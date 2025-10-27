package com.clientbilling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.clientbilling.model.Project;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByClientId(Long clientId);
    Optional<Project> findByProjectIdNo(String projectIdNo);
    
}