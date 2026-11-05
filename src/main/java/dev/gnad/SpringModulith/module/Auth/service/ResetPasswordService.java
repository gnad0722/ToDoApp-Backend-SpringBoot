package dev.gnad.SpringModulith.module.Auth.service;

import dev.gnad.SpringModulith.common.api.exception.NotFoundException;
import dev.gnad.SpringModulith.module.Auth.domain.entity.RefreshToken;
import dev.gnad.SpringModulith.module.Auth.domain.entity.ResetPasswordToken;
import dev.gnad.SpringModulith.module.Auth.domain.entity.User;
import dev.gnad.SpringModulith.module.Auth.internal.repository.ResetPasswordTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class ResetPasswordService {
    private final ResetPasswordTokenRepository resetPasswordTokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${security.jwt.reset-password-token-expiration}")
    private long tokenExpiration;

    public ResetPasswordService(ResetPasswordTokenRepository resetPasswordTokenRepository) {
        this.resetPasswordTokenRepository = resetPasswordTokenRepository;
    }


    private String generateResetToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    @Transactional
    public String createResetToken(User user){
        String rawToken = this.generateResetToken();

        ResetPasswordToken resetPasswordToken = new ResetPasswordToken();


        resetPasswordToken.setTokenHash(this.hashToken(rawToken));
        resetPasswordToken.setUser(user);
        resetPasswordToken.setExpiresAt(
                Instant.now().plusMillis(60*15)
        );
        resetPasswordTokenRepository.save(resetPasswordToken);
        return rawToken;
    }


    @Transactional
    public ResetPasswordToken validateToken(String rawToken){
        String tokenHash = this.hashToken(rawToken);

        ResetPasswordToken resetPasswordToken = this.resetPasswordTokenRepository
                .findByTokenHash(tokenHash).orElseThrow(
                        ()->new NotFoundException("Invalid Reset Token")
                );

        if (resetPasswordToken.isExpired()){
            throw new RuntimeException("Reset token is invalid");
        }

        if (resetPasswordToken.isUsed()){
            throw new RuntimeException("Reset token is invalid");
        }
        return resetPasswordToken;
    }


    @Transactional
    public void revoke(ResetPasswordToken resetPasswordToken){
        resetPasswordToken.setUsedAt(Instant.now());

        this.resetPasswordTokenRepository.save(resetPasswordToken);
    }
}
