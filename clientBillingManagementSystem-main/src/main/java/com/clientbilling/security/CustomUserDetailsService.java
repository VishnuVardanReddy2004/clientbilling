package com.clientbilling.security;

import com.clientbilling.model.Admin;
import com.clientbilling.model.Client;
import com.clientbilling.model.TeamLead;
import com.clientbilling.model.Employee;
import com.clientbilling.repository.AdminRepository;
import com.clientbilling.repository.ClientRepository;
import com.clientbilling.repository.TeamLeadRepository;
import com.clientbilling.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired private AdminRepository adminRepository;
    @Autowired private ClientRepository clientRepository;
    @Autowired private TeamLeadRepository teamLeadRepository;
    @Autowired private EmployeeRepository employeeRepository;

    // ✅ LOGIN LOAD BY USERNAME
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("[LOGIN] Looking up user: " + username);

        //  Admin
        Optional<Admin> admin = adminRepository.findByUsername(username);
        if (admin.isEmpty()) admin = adminRepository.findByEmail(username);
        if (admin.isPresent()) return buildUser(admin.get().getUsername(), admin.get().getPassword(), admin.get().getRole());

        //  Client
        Optional<Client> client = clientRepository.findByUsername(username);
        if (client.isEmpty()) client = clientRepository.findByEmail(username);
        if (client.isPresent()) return buildUser(client.get().getUsername(), client.get().getPassword(), client.get().getRole());

        //  Team Lead
        Optional<TeamLead> tl = teamLeadRepository.findByUsername(username);
        if (tl.isEmpty()) tl = teamLeadRepository.findByEmail(username);
        if (tl.isPresent()) return buildUser(tl.get().getUsername(), tl.get().getPassword(), tl.get().getRole());

        //  Employee
        Optional<Employee> emp = employeeRepository.findByUsername(username);
        if (emp.isEmpty()) emp = employeeRepository.findByEmail(username);
        if (emp.isPresent()) return buildUser(emp.get().getUsername(), emp.get().getPassword(), emp.get().getRole());

        throw new UsernameNotFoundException("User not found: " + username);
    }

    private UserDetails buildUser(String username, String password, String role) {
        String finalRole = role.startsWith("ROLE_") ? role.substring(5) : role;
        return User.builder()
                .username(username)
                .password(password)
                .roles(finalRole)
                .build();
    }

    // ✅ Helper class to store user ref
    public static class UserRef {
        private Long id;
        private String type;
        private String email;

        public UserRef(Long id, String type, String email) {
            this.id = id;
            this.type = type;
            this.email = email;
        }

        public Long getId() { return id; }
        public String getType() { return type; }
        public String getEmail() { return email; }
    }

    // ✅ Find user entity (for Forgot Password)
    public UserRef findUserEntityByUsernameOrEmail(String usernameOrEmail) {
        Optional<Admin> admin = adminRepository.findByUsername(usernameOrEmail);
        if (admin.isEmpty()) admin = adminRepository.findByEmail(usernameOrEmail);
        if (admin.isPresent()) {
            var a = admin.get();
            return new UserRef(a.getId(), "ADMIN", a.getEmail());
        }

        Optional<Client> client = clientRepository.findByUsername(usernameOrEmail);
        if (client.isEmpty()) client = clientRepository.findByEmail(usernameOrEmail);
        if (client.isPresent()) {
            var c = client.get();
            return new UserRef(c.getId(), "CLIENT", c.getEmail());
        }

        Optional<TeamLead> tl = teamLeadRepository.findByUsername(usernameOrEmail);
        if (tl.isEmpty()) tl = teamLeadRepository.findByEmail(usernameOrEmail);
        if (tl.isPresent()) {
            var t = tl.get();
            return new UserRef(t.getId(), "TEAM_LEAD", t.getEmail());
        }

        Optional<Employee> emp = employeeRepository.findByUsername(usernameOrEmail);
        if (emp.isEmpty()) emp = employeeRepository.findByEmail(usernameOrEmail);
        if (emp.isPresent()) {
            var e = emp.get();
            return new UserRef(e.getId(), "EMPLOYEE", e.getEmail());
        }

        throw new UsernameNotFoundException("No user found with identifier: " + usernameOrEmail);
    }

    // ✅ Update user password based on type and ID
    public void updateUserPasswordByIdAndType(Long userId, String type, String encodedPassword) {
        switch (type.toUpperCase()) {
            case "ADMIN" -> {
                Admin a = adminRepository.findById(userId).orElseThrow();
                a.setPassword(encodedPassword);
                adminRepository.save(a);
            }
            case "CLIENT" -> {
                Client c = clientRepository.findById(userId).orElseThrow();
                c.setPassword(encodedPassword);
                clientRepository.save(c);
            }
            case "TEAM_LEAD" -> {
                TeamLead t = teamLeadRepository.findById(userId).orElseThrow();
                t.setPassword(encodedPassword);
                teamLeadRepository.save(t);
            }
            case "EMPLOYEE" -> {
                Employee e = employeeRepository.findById(userId).orElseThrow();
                e.setPassword(encodedPassword);
                employeeRepository.save(e);
            }
            default -> throw new IllegalArgumentException("Unknown user type: " + type);
        }
    }
}
