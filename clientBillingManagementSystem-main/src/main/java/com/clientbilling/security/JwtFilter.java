package com.clientbilling.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.function.Predicate;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired private JwtUtil jwtUtil;
    @Autowired private CustomUserDetailsService userDetailsService;

    /** Public (permitAll) routes and non-auth cases we skip */
    private final Predicate<String> isPublicPath = path ->
            path.startsWith("/api/auth/") ||
                    path.equals("/api/admin/register") ||
                    path.startsWith("/v3/api-docs") ||
                    path.startsWith("/swagger-ui") ||
                    path.equals("/swagger-ui.html") ||
                    path.startsWith("/actuator/health") ||
                    path.equals("/error") ||
                    path.startsWith("/assets/") ||        // static (optional)
                    path.startsWith("/static/");          // static (optional)

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        final String method = request.getMethod();
        final String uri    = request.getRequestURI();

        // 1) Always let CORS preflight through
        if ("OPTIONS".equalsIgnoreCase(method)) {
            chain.doFilter(request, response);
            return;
        }

        // 2) Skip all public endpoints entirely
        if (isPublicPath.test(uri)) {
            chain.doFilter(request, response);
            return;
        }

        // 3) Extract and validate token if present
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // No token: leave SecurityContext empty and continue.
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String username = null;
        try {
            username = jwtUtil.extractUsername(token);
        } catch (Exception ignored) {
            // Bad token format: continue without auth; Security will return 401 for protected paths.
            chain.doFilter(request, response);
            return;
        }

        // 4) If not already authenticated, validate and set authentication
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails user = userDetailsService.loadUserByUsername(username);
                if (jwtUtil.validateToken(token)) { // keep this 1-arg validator
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception ignored) {
                // Any failure -> proceed without setting auth
            }
        }

        // 5) Continue the chain
        chain.doFilter(request, response);
    }
}
