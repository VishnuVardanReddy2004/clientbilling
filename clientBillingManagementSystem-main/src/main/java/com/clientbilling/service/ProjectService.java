package com.clientbilling.service;

import com.clientbilling.model.Project;
import com.clientbilling.model.Admin;
import com.clientbilling.model.Client;
import com.clientbilling.repository.ProjectRepository;
import com.clientbilling.repository.AdminRepository;
import com.clientbilling.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Transactional
    public Project addProject(Project project) {

        // Attach existing Admin
        if (project.getAdmin() != null && project.getAdmin().getId() != null) {
            Admin existingAdmin = adminRepository.findById(project.getAdmin().getId())
                    .orElseThrow(() -> new RuntimeException("Admin not found"));
            project.setAdmin(existingAdmin);
            existingAdmin.getProjects().add(project);
        }

        // Attach existing Client
        if (project.getClient() != null && project.getClient().getId() != null) {
            Client existingClient = clientRepository.findById(project.getClient().getId())
                    .orElseThrow(() -> new RuntimeException("Client not found"));
            project.setClient(existingClient);
            existingClient.getProjects().add(project);
        }

        return projectRepository.save(project);
    }

    @Transactional(readOnly = true)
    public Project getProjectById(Long id) {
        return projectRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @Transactional
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }
}
