package com.clientbilling.controller;

import com.clientbilling.model.Admin;
import com.clientbilling.service.AdminService;
import com.clientbilling.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // ✅ Register Admin
    @PostMapping("/register")
    public ResponseEntity<?> registerAdmin(@RequestBody Admin admin) {

        // Allow first Admin (no authentication required)
        if (adminService.getAllAdmins().isEmpty()) {
            admin.setRole("ROLE_ADMIN");
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));
            return ResponseEntity.ok(adminService.registerAdmin(admin));
        }

        // Only an existing Admin can create another Admin
        String currentRole = securityUtil.getCurrentUserRole();
        if (currentRole == null || !currentRole.equals("ROLE_ADMIN")) {
            return ResponseEntity.status(403).body("Access Denied: Only Admin can register another Admin");
        }

        admin.setRole("ROLE_ADMIN");
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        return ResponseEntity.ok(adminService.registerAdmin(admin));
    }

    // ✅ Get all admins
    @GetMapping("/all")
    public ResponseEntity<?> getAllAdmins() {
        String currentRole = securityUtil.getCurrentUserRole();
        if (currentRole == null || !currentRole.equals("ROLE_ADMIN")) {
            return ResponseEntity.status(403).body("Access Denied");
        }
        return ResponseEntity.ok(adminService.getAllAdmins());
    }

    // ✅ Get single admin by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getAdminById(@PathVariable Long id) {
        String currentRole = securityUtil.getCurrentUserRole();
        if (currentRole == null || !currentRole.equals("ROLE_ADMIN")) {
            return ResponseEntity.status(403).body("Access Denied");
        }

        Admin admin = adminService.getAdminById(id);
        if (admin != null) return ResponseEntity.ok(admin);
        return ResponseEntity.notFound().build();
    }

    // ✅ Delete admin
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAdmin(@PathVariable Long id) {
        String currentRole = securityUtil.getCurrentUserRole();
        if (currentRole == null || !currentRole.equals("ROLE_ADMIN")) {
            return ResponseEntity.status(403).body("Access Denied");
        }

        adminService.deleteAdmin(id);
        return ResponseEntity.ok("Admin deleted successfully");
    }
}