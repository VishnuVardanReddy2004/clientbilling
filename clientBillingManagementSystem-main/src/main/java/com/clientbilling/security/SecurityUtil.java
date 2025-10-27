package com.clientbilling.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SecurityUtil {

    // Get logged-in username
    public String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? auth.getName() : null;
    }

    // Get all roles of current user
    public Set<String> getCurrentUserRoles() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && !auth.getAuthorities().isEmpty()) {
            return auth.getAuthorities()
                       .stream()
                       .map(GrantedAuthority::getAuthority) // returns ROLE_ADMIN, ROLE_CLIENT...
                       .collect(Collectors.toSet());
        }
        return Set.of();
    }

    // Check if user has any of the roles
    public boolean hasAnyRole(String... roles) {
        Set<String> userRoles = getCurrentUserRoles();
        for (String role : roles) {
            if (userRoles.contains(role)) return true;
        }
        return false;
    }

    // ✅ Get current single role (used in your controller)
    public String getCurrentUserRole() {
        Set<String> roles = getCurrentUserRoles();
        return roles.isEmpty() ? "UNKNOWN" : roles.iterator().next();
    }
}
