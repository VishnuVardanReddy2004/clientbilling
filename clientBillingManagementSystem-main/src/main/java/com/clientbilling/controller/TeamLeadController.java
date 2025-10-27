package com.clientbilling.controller;

import com.clientbilling.model.TeamLead;
import com.clientbilling.service.TeamLeadService;
import com.clientbilling.security.SecurityUtil;
import com.clientbilling.security.CustomUserDetailsService;
import com.clientbilling.security.JwtUtil;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teamlead")
public class TeamLeadController {

    @Autowired
    private TeamLeadService teamLeadService;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // ✅ TeamLead login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            System.out.println("Loaded TeamLead: " + userDetails.getUsername() + ", hashed password: " + userDetails.getPassword());

            authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

            String role = userDetails.getAuthorities().stream()
                                     .findFirst()
                                     .map(a -> a.getAuthority())
                                     .orElse("ROLE_TEAMLEAD");

            String token = jwtUtil.generateToken(username, role);

            return ResponseEntity.ok(Map.of(
                "username", username,
                "role", role,
                "token", token
            ));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid Credentials"));
        }
    }

    // ✅ Register TeamLead
    @PostMapping("/register")
    public ResponseEntity<?> registerTeamLead(@RequestBody TeamLead teamLead) {
        if (!"ROLE_ADMIN".equals(securityUtil.getCurrentUserRole())) {
            return ResponseEntity.status(403).body("Access Denied");
        }

        if (teamLead.getRole() == null || teamLead.getRole().isEmpty()) {
            teamLead.setRole("ROLE_TEAMLEAD");
        }

        System.out.println("Password before encode: " + teamLead.getPassword());
        teamLead.setPassword(passwordEncoder.encode(teamLead.getPassword()));
        System.out.println("Password after encode: " + teamLead.getPassword());

        return ResponseEntity.ok(teamLeadService.registerTeamLead(teamLead));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllTeamLeads() {
        String role = securityUtil.getCurrentUserRole();
        if(!role.equals("ROLE_ADMIN") && !role.equals("ROLE_TEAMLEAD"))
            return ResponseEntity.status(403).body("Access Denied");
        return ResponseEntity.ok(teamLeadService.getAllTeamLeads());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTeamLeadById(@PathVariable Long id) {
        String role = securityUtil.getCurrentUserRole();
        if(!role.equals("ROLE_ADMIN") && !role.equals("ROLE_TEAMLEAD"))
            return ResponseEntity.status(403).body("Access Denied");
        TeamLead teamLead = teamLeadService.getTeamLeadById(id);
        if(teamLead != null) return ResponseEntity.ok(teamLead);
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTeamLead(@PathVariable Long id) {
        if(!"ROLE_ADMIN".equals(securityUtil.getCurrentUserRole()))
            return ResponseEntity.status(403).body("Access Denied");
        teamLeadService.deleteTeamLead(id);
        return ResponseEntity.ok("TeamLead deleted successfully");
    }
}