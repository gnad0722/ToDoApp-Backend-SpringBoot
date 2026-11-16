package dev.gnad.SpringModulith.common.token.service;

import dev.gnad.SpringModulith.common.api.exception.NotFoundException;
import dev.gnad.SpringModulith.common.cache.constants.CacheName;
import dev.gnad.SpringModulith.common.token.domain.entity.BaseToken;
import dev.gnad.SpringModulith.common.token.domain.entity.RefreshToken;
import dev.gnad.SpringModulith.common.token.factory.TokenFactory;
import dev.gnad.SpringModulith.common.token.internal.repository.RefreshTokenRepository;
import dev.gnad.SpringModulith.common.token.utility.TokenCommon;
import dev.gnad.SpringModulith.module.Auth.domain.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.sql.Ref;
import java.time.Instant;

@Service
@CacheConfig(cacheNames = CacheName.RefreshToken)
public class RefreshTokenService implements TokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    @Override
//    @CachePut(
//            value = "token",
//            key = "#result.tokenHash"
//    )
    public BaseToken createToken(User user, TokenFactory factory){
        RefreshToken token = (RefreshToken)  factory.createToken(user);
        this.refreshTokenRepository.save(token);
        return token;
    }

//    @Cacheable(
//            value = "tokens",
//            keyGenerator = "tokenKeyGenerator"
//    )
    private BaseToken getToken(String rawToken){
        String hashToken = TokenCommon.hashToken(rawToken);

        return this.refreshTokenRepository
                .findByTokenHash(hashToken).orElseThrow(
                        ()->new NotFoundException("Invalid Refresh Token")
                );
    }

    @Transactional
    @Override
    public BaseToken validateToken(String rawToken){

       BaseToken token = this.getToken(rawToken);
        if (token.isExpired()){
            throw new RuntimeException("Refresh Token is invalid");
        }

        if (token.isRevoked()){
            throw new RuntimeException("Refresh Token is invalid");
        }
        return token;
    }

    @Transactional
    @Override
    public void revoke(String rawToken){
           RefreshToken token = (RefreshToken) this.validateToken(rawToken);
            token.setRevokedAt(Instant.now());
            this.refreshTokenRepository.save(token);
    }
}
