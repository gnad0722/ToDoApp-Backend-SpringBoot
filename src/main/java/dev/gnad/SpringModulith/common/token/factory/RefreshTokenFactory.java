package dev.gnad.SpringModulith.common.token.factory;

import dev.gnad.SpringModulith.common.cache.constants.CacheName;
import dev.gnad.SpringModulith.common.token.domain.entity.BaseToken;
import dev.gnad.SpringModulith.common.token.domain.entity.RefreshToken;
import dev.gnad.SpringModulith.common.token.strategy.RefreshTokenStrategy;

import dev.gnad.SpringModulith.common.token.utility.TokenCommon;
import dev.gnad.SpringModulith.module.Auth.domain.entity.User;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@Component
public class RefreshTokenFactory implements TokenFactory{

    private final SecureRandom secureRandom = new SecureRandom();

    private final RefreshTokenStrategy refreshTokenStrategy;

    public RefreshTokenFactory(RefreshTokenStrategy refreshTokenStrategy) {
        this.refreshTokenStrategy = refreshTokenStrategy;
    }

    private String generateRefreshToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }



    @Override
    public BaseToken createToken(User user) {
        String rawToken = this.generateRefreshToken();

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setTokenRaw(rawToken);
        refreshToken.setTokenHash(TokenCommon.hashToken(rawToken));
        refreshToken.setUser(user);
        refreshToken.setExpireAt(
                Instant.now().plusMillis(refreshTokenStrategy.getExpiration())
        );
        return refreshToken;
    }
}
