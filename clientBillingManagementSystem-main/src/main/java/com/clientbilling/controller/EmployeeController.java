package com.clientbilling.controller;

import com.clientbilling.model.Employee;
import com.clientbilling.service.EmployeeService;
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
@RequestMapping("/api/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

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

    // ✅ Employee login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        try {
            // Load user first
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            System.out.println("Loaded Employee: " + userDetails.getUsername() + ", hashed password: " + userDetails.getPassword());

            // Authenticate
            authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

            String role = userDetails.getAuthorities().stream()
                                     .findFirst()
                                     .map(a -> a.getAuthority())
                                     .orElse("ROLE_EMPLOYEE");

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

    // ✅ Register Employee
    @PostMapping("/register")
    public ResponseEntity<?> registerEmployee(@RequestBody Employee employee) {
        if (!"ROLE_ADMIN".equals(securityUtil.getCurrentUserRole())) {
            return ResponseEntity.status(403).body("Access Denied");
        }

        System.out.println("Password before encode: " + employee.getPassword());
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        System.out.println("Password after encode: " + employee.getPassword());

        return ResponseEntity.ok(employeeService.registerEmployee(employee));
    }


    @GetMapping("/all")
    public ResponseEntity<?> getAllEmployees() {
        String role = securityUtil.getCurrentUserRole();
        if(!role.equals("ROLE_ADMIN") && !role.equals("ROLE_TEAMLEAD") && !role.equals("ROLE_EMPLOYEE"))
            return ResponseEntity.status(403).body("Access Denied");
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable Long id) {
        String role = securityUtil.getCurrentUserRole();
        if(!role.equals("ROLE_ADMIN") && !role.equals("ROLE_TEAMLEAD") && !role.equals("ROLE_EMPLOYEE"))
            return ResponseEntity.status(403).body("Access Denied");
        Employee emp = employeeService.getEmployeeById(id);
        if(emp != null) return ResponseEntity.ok(emp);
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam String status) {
        String role = securityUtil.getCurrentUserRole();
        if(!role.equals("ROLE_ADMIN") && !role.equals("ROLE_TEAMLEAD"))
            return ResponseEntity.status(403).body("Access Denied");
        Employee emp = employeeService.updateStatus(id, status);
        if(emp != null) return ResponseEntity.ok(emp);
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        if(!"ROLE_ADMIN".equals(securityUtil.getCurrentUserRole()))
            return ResponseEntity.status(403).body("Access Denied");
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok("Employee deleted successfully");
    }
}