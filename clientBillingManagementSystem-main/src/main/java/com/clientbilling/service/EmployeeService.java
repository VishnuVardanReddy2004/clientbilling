package com.clientbilling.service;

import com.clientbilling.model.Employee;
import com.clientbilling.model.Admin;
import com.clientbilling.model.TeamLead;
import com.clientbilling.model.Project;
import com.clientbilling.repository.EmployeeRepository;
import com.clientbilling.repository.AdminRepository;
import com.clientbilling.repository.TeamLeadRepository;
import com.clientbilling.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private TeamLeadRepository teamLeadRepository;

    @Autowired
    private ProjectRepository projectRepository;

    // Register employee with email validation
    public Employee registerEmployee(Employee employee) {

        // ✅ Validate email
        if (employee.getEmail() == null || employee.getEmail().isEmpty()) {
            throw new RuntimeException("Email is required");
        }

        // ✅ Optional: check for uniqueness
        Optional<Employee> existing = employeeRepository.findByEmail(employee.getEmail());
        if (existing.isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // Attach existing Admin
        if (employee.getAdmin() != null && employee.getAdmin().getId() != null) {
            Admin admin = adminRepository.findById(employee.getAdmin().getId())
                    .orElseThrow(() -> new RuntimeException("Admin not found"));
            employee.setAdmin(admin);
        }

        // Attach existing TeamLead
        if (employee.getTeamLead() != null && employee.getTeamLead().getId() != null) {
            TeamLead teamLead = teamLeadRepository.findById(employee.getTeamLead().getId())
                    .orElseThrow(() -> new RuntimeException("TeamLead not found"));
            employee.setTeamLead(teamLead);
        }

        // Attach existing Project
        if (employee.getProject() != null && employee.getProject().getId() != null) {
            Project project = projectRepository.findById(employee.getProject().getId())
                    .orElseThrow(() -> new RuntimeException("Project not found"));
            employee.setProject(project);
        }

        return employeeRepository.save(employee);
    }

    // Get employee by ID
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id).orElse(null);
    }

    // Get all employees
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    // Delete employee
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    // Update employee status
    public Employee updateStatus(Long id, String status) {
        Employee emp = employeeRepository.findById(id).orElse(null);
        if (emp != null) {
            emp.setStatus(status);
            return employeeRepository.save(emp);
        }
        return null;
    }
}
