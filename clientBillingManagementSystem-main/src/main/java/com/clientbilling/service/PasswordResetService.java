package com.clientbilling.service;

import com.clientbilling.model.PasswordResetToken;
import com.clientbilling.repository.PasswordResetTokenRepository;
import com.clientbilling.security.CustomUserDetailsService;
import com.clientbilling.util.HashUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository tokenRepo;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final SecureRandom RNG = new SecureRandom();

    // ✅ Create token (store hashed version)
    public String createResetToken(String usernameOrEmail, long ttlSeconds) {
        var userRef = userDetailsService.findUserEntityByUsernameOrEmail(usernameOrEmail);

        String rawToken = generateRawToken();
        String tokenHash = HashUtils.sha256Hex(rawToken);

        PasswordResetToken token = new PasswordResetToken();
        token.setUserId(userRef.getId());
        token.setUserType(userRef.getType());
        token.setTokenHash(tokenHash);
        token.setExpiresAt(Instant.now().plusSeconds(ttlSeconds));
        token.setUsed(false);
        tokenRepo.save(token);

        // 🔒 Removed all token logging — no System.out.println / log.info(rawToken)
        return rawToken;
    }

    // ✅ Validate token & reset password
    public void setNewPassword(String rawToken, String newPassword) {
        String tokenHash = HashUtils.sha256Hex(rawToken);
        var tokenOpt = tokenRepo.findByTokenHash(tokenHash);
        if (tokenOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid or expired token");
        }
        var t = tokenOpt.get();
        if (t.isUsed() || t.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Token is invalid or expired");
        }

        t.setUsed(true);
        tokenRepo.save(t);

        userDetailsService.updateUserPasswordByIdAndType(
                t.getUserId(),
                t.getUserType(),
                passwordEncoder.encode(newPassword)
        );
    }

    private String generateRawToken() {
        byte[] buf = new byte[48];
        RNG.nextBytes(buf);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
    }
}
