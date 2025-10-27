package com.clientbilling.service;

import com.clientbilling.model.TeamLead;
import com.clientbilling.model.Project;
import com.clientbilling.model.Admin;
import com.clientbilling.model.Client;
import com.clientbilling.repository.TeamLeadRepository;
import com.clientbilling.repository.ProjectRepository;
import com.clientbilling.repository.AdminRepository;
import com.clientbilling.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TeamLeadService {

    @Autowired
    private TeamLeadRepository teamLeadRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ClientRepository clientRepository;

    // Register TeamLead with email validation
    public TeamLead registerTeamLead(TeamLead teamLead) {

        // ✅ Validate email
        if (teamLead.getEmail() == null || teamLead.getEmail().isEmpty()) {
            throw new RuntimeException("Email is required");
        }

        // ✅ Optional: check uniqueness
        Optional<TeamLead> existing = teamLeadRepository.findByEmail(teamLead.getEmail());
        if (existing.isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // Set role if null
        if (teamLead.getRole() == null || teamLead.getRole().isEmpty()) {
            teamLead.setRole("ROLE_TEAMLEAD");
        }

        // Attach existing Admin
        if (teamLead.getAdmin() != null && teamLead.getAdmin().getId() != null) {
            Admin admin = adminRepository.findById(teamLead.getAdmin().getId())
                    .orElseThrow(() -> new RuntimeException("Admin not found"));
            teamLead.setAdmin(admin);
        }

        // Attach existing Client
        if (teamLead.getClient() != null && teamLead.getClient().getId() != null) {
            Client client = clientRepository.findById(teamLead.getClient().getId())
                    .orElseThrow(() -> new RuntimeException("Client not found"));
            teamLead.setClient(client);
        }

        // Attach existing Projects
        List<Project> projects = new ArrayList<>();
        if (teamLead.getProjects() != null) {
            for (Project p : teamLead.getProjects()) {
                if (p.getId() != null) {
                    Project project = projectRepository.findById(p.getId())
                            .orElseThrow(() -> new RuntimeException("Project not found"));
                    project.setTeamLead(teamLead); // maintain bidirectional
                    projects.add(project);
                }
            }
        }
        teamLead.setProjects(projects);

        return teamLeadRepository.save(teamLead);
    }

    // Get TeamLead by ID
    public TeamLead getTeamLeadById(Long id) {
        return teamLeadRepository.findById(id).orElse(null);
    }

    // Get all TeamLeads
    public List<TeamLead> getAllTeamLeads() {
        return teamLeadRepository.findAll();
    }

    // Delete TeamLead
    public void deleteTeamLead(Long id) {
        teamLeadRepository.deleteById(id);
    }
}
