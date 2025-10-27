package com.clientbilling.controller;

import com.clientbilling.security.CustomUserDetailsService;
import com.clientbilling.security.JwtUtil;
import com.clientbilling.service.MailService;
import com.clientbilling.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // ✅ Frontend URL for password reset link
    private static final String RESET_LINK_BASE = "http://localhost:3000/reset-password?token=";

    @Autowired private AuthenticationManager authManager;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private CustomUserDetailsService userDetailsService;
    @Autowired private PasswordResetService passwordResetService;
    @Autowired private MailService mailService;

    // ✅ STEP 1: Forgot Password (user receives link by email)
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> req) {
        String usernameOrEmail = req.get("usernameOrEmail");

        if (usernameOrEmail == null || usernameOrEmail.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "usernameOrEmail is required"));
        }

        try {
            // Generate token (valid for 2 hours)
            String rawToken = passwordResetService.createResetToken(usernameOrEmail.trim(), 2 * 60 * 60);

            // Encode and build reset link for frontend
            String encoded = URLEncoder.encode(rawToken, StandardCharsets.UTF_8);
            String resetLink = RESET_LINK_BASE + encoded;

            // Find email of user and send link via SMTP
            var ref = userDetailsService.findUserEntityByUsernameOrEmail(usernameOrEmail.trim());
            mailService.sendPasswordResetEmail(ref.getEmail(), resetLink);

            // ✅ Return only a generic message — token not exposed to Postman
            return ResponseEntity.ok(Map.of(
                    "message", "If an account exists for this identifier, a reset link has been sent to the registered email."
            ));

        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "message", "If an account exists for this identifier, a reset link has been sent to the registered email."
            ));
        }
    }

    // ✅ STEP 2: Reset Password (user opens email → resets)
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> req) {
        String token = req.get("token");
        String newPassword = req.get("newPassword");

        if (token == null || token.isBlank() || newPassword == null || newPassword.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "token and newPassword are required"));
        }

        try {
            passwordResetService.setNewPassword(token, newPassword);
            return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ STEP 3: Login after password reset
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        if (username == null || password == null || username.isBlank() || password.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "username and password are required"));
        }

        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            String role = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(a -> a.getAuthority())
                    .orElse("ROLE_USER");
            String token = jwtUtil.generateToken(username, role);

            return ResponseEntity.ok(Map.of(
                    "username", username,
                    "role", role,
                    "token", token
            ));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
