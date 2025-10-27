package com.clientbilling.controller;

import com.clientbilling.model.Project;
import com.clientbilling.service.ProjectService;
import com.clientbilling.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private SecurityUtil securityUtil;

    @PostMapping("/add")
    
    public ResponseEntity<?> addProject(@RequestBody Project project) {
        String role = securityUtil.getCurrentUserRole();
        if(!role.equals("ROLE_ADMIN") && !role.equals("ROLE_CLIENT") && !role.equals("ROLE_TEAMLEAD"))
            return ResponseEntity.status(403).body("Access Denied");

        try {
            // Save project safely with employees attached
            Project savedProject = projectService.addProject(project);
            return ResponseEntity.ok(savedProject);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllProjects() {
        String role = securityUtil.getCurrentUserRole();
        if(!role.equals("ROLE_ADMIN") && !role.equals("ROLE_CLIENT") && !role.equals("ROLE_TEAMLEAD") && !role.equals("ROLE_EMPLOYEE"))
            return ResponseEntity.status(403).body("Access Denied");
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProjectById(@PathVariable Long id) {
        String role = securityUtil.getCurrentUserRole();
        if(!role.equals("ROLE_ADMIN") && !role.equals("ROLE_CLIENT") && !role.equals("ROLE_TEAMLEAD") && !role.equals("ROLE_EMPLOYEE"))
            return ResponseEntity.status(403).body("Access Denied");

        Project project = projectService.getProjectById(id);
        if(project != null) return ResponseEntity.ok(project);
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id) {
        if(!"ROLE_ADMIN".equals(securityUtil.getCurrentUserRole()))
            return ResponseEntity.status(403).body("Access Denied");

        projectService.deleteProject(id);
        return ResponseEntity.ok("Project deleted successfully");
    }
}