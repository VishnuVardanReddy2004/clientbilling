package com.clientbilling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.clientbilling.model.Employee;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Employee findByEmpIdNo(String empIdNo);
    List<Employee> findByTeamLeadId(Long teamLeadId);
  
    Optional<Employee> findByUsername(String username);
	Optional<Employee> findByEmail(String email);
}