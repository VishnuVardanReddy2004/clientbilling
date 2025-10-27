package com.clientbilling.service;

import com.clientbilling.model.Admin;
import com.clientbilling.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    // Register admin with email validation
    public Admin registerAdmin(Admin admin) {

        // ✅ Validate email
        if (admin.getEmail() == null || admin.getEmail().isEmpty()) {
            throw new RuntimeException("Email is required");
        }

        // ✅ Optional: check uniqueness
        Optional<Admin> existing = adminRepository.findByEmail(admin.getEmail());
        if (existing.isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        return adminRepository.save(admin);
    }

    // Get admin by ID
    public Admin getAdminById(Long id) {
        return adminRepository.findById(id).orElse(null);
    }

    // Get all admins with lazy collections initialized
    @Transactional(readOnly = true)
    public List<Admin> getAllAdmins() {
        List<Admin> admins = adminRepository.findAll();
        admins.forEach(admin -> {
            admin.getClients().size();
            admin.getProjects().size();
            admin.getInvoices().size();
        });
        return admins;
    }

    // Delete admin
    public void deleteAdmin(Long id) {
        adminRepository.deleteById(id);
    }
}
