package com.clientbilling.controller;

import com.clientbilling.model.Client;
import com.clientbilling.service.ClientService;
import com.clientbilling.security.SecurityUtil;
import com.clientbilling.security.JwtUtil;
import com.clientbilling.security.CustomUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@RestController
@RequestMapping("/api/client")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        try {
            // Load user details from DB first
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            System.out.println("Loaded user: " + userDetails.getUsername() + ", hashed password: " + userDetails.getPassword());

            // Authenticate credentials using AuthenticationManager
            authManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );

            String role = userDetails.getAuthorities().stream()
                                     .findFirst()
                                     .map(a -> a.getAuthority())
                                     .orElse("ROLE_CLIENT");

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

    // ✅ Register Client
    @PostMapping("/register")
    public ResponseEntity<?> registerClient(@RequestBody Client client) {
        if (!"ROLE_ADMIN".equals(securityUtil.getCurrentUserRole())) {
            return ResponseEntity.status(403).body("Access Denied: Only Admin can register a client");
        }

        // Default role
        if (client.getRole() == null || client.getRole().isEmpty()) {
            client.setRole("ROLE_CLIENT");
        }

        // Hash password before saving
        System.out.println("Password before encode: " + client.getPassword());
        client.setPassword(passwordEncoder.encode(client.getPassword()));
        System.out.println("Password after encode: " + client.getPassword());

        Client savedClient = clientService.registerClient(client);
        return ResponseEntity.ok(savedClient);
    }


    // 3️⃣ Get all clients
    @GetMapping("/all")
    public ResponseEntity<?> getAllClients() {
        String role = securityUtil.getCurrentUserRole();
        if (!role.equals("ROLE_ADMIN") && !role.equals("ROLE_CLIENT")) {
            return ResponseEntity.status(403).body("Access Denied");
        }
        return ResponseEntity.ok(clientService.getAllClients());
    }

    // 4️⃣ Get client by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getClientById(@PathVariable Long id) {
        String role = securityUtil.getCurrentUserRole();
        if (!role.equals("ROLE_ADMIN") && !role.equals("ROLE_CLIENT")) {
            return ResponseEntity.status(403).body("Access Denied");
        }
        Client client = clientService.getClientById(id);
        if (client != null) return ResponseEntity.ok(client);
        return ResponseEntity.notFound().build();
    }

    // 5️⃣ Delete client
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClient(@PathVariable Long id) {
        if (!"ROLE_ADMIN".equals(securityUtil.getCurrentUserRole())) {
            return ResponseEntity.status(403).body("Access Denied");
        }
        clientService.deleteClient(id);
        return ResponseEntity.ok("Client deleted successfully");
    }
}