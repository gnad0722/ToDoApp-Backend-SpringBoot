package dev.gnad.SpringModulith.module.Auth.service;

import dev.gnad.SpringModulith.common.api.exception.NotFoundException;
import dev.gnad.SpringModulith.module.Auth.domain.entity.RefreshToken;
import dev.gnad.SpringModulith.module.Auth.domain.entity.User;
import dev.gnad.SpringModulith.module.Auth.internal.repository.RefreshTokenRepository;
import dev.gnad.SpringModulith.module.Auth.internal.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class RefreshTokenService {


    private final RefreshTokenRepository refreshTokenRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${security.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;


    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }


    private String generateRefreshToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
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
    public String createRefreshToken(User user){
        String rawToken = this.generateRefreshToken();

        RefreshToken refreshToken = new RefreshToken();


        refreshToken.setTokenHash(this.hashToken(rawToken));
        refreshToken.setUser(user);
        refreshToken.setExpireAt(
                Instant.now().plusMillis(refreshTokenExpiration)
        );
        refreshTokenRepository.save(refreshToken);
        return rawToken;
    }

    @Transactional
    public RefreshToken validateRefreshToken(String rawToken){
        String tokenHash = this.hashToken(rawToken);

        RefreshToken refreshToken = this.refreshTokenRepository
                .findByTokenHash(tokenHash).orElseThrow(
                        ()->new NotFoundException("Invalid Refresh Token")
                );

        if (refreshToken.isExpired()){
            throw new RuntimeException("Refresh token expired");
        }

        if (refreshToken.isRevoked()){
            throw new RuntimeException("Refresh token revoked");
        }
        return refreshToken;
    }

    @Transactional
    public void revokedToken(RefreshToken refreshToken){

        refreshToken.setRevokedAt(Instant.now());
        this.refreshTokenRepository.save(refreshToken);
    }
}
