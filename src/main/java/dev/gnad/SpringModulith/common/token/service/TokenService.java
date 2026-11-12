package dev.gnad.SpringModulith.common.token.service;

import dev.gnad.SpringModulith.common.api.exception.NotFoundException;
import dev.gnad.SpringModulith.common.token.domain.entity.BaseToken;
import dev.gnad.SpringModulith.common.token.factory.TokenFactory;
import dev.gnad.SpringModulith.common.token.internal.repository.BaseTokenRepository;
import dev.gnad.SpringModulith.common.token.utility.TokenCommon;
import dev.gnad.SpringModulith.module.Auth.domain.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service

public class TokenService {

    private final BaseTokenRepository baseTokenRepository;

    public TokenService(BaseTokenRepository baseTokenRepository) {
        this.baseTokenRepository = baseTokenRepository;
    }

    @Transactional
    public BaseToken createToken(User user, TokenFactory factory){
        BaseToken token = factory.createToken(user);
        this.baseTokenRepository.save(token);
        return token;
    }


    @Transactional
    public BaseToken validateToken(String rawToken){
            String hashToken = TokenCommon.hashToken(rawToken);

        BaseToken baseToken = this.baseTokenRepository
                .findByTokenHash(hashToken).orElseThrow(
                        ()->new NotFoundException("Invalid Token")
                );

        if (baseToken.isExpired()){
            throw new RuntimeException("Token is invalid");
        }

        if (baseToken.isRevoked()){
            throw new RuntimeException("Token is invalid");
        }
        return baseToken;
    }

    @Transactional
    public void revoke(String rawToken){
            BaseToken token = this.validateToken(rawToken);
            token.setRevokedAt(Instant.now());
            this.baseTokenRepository.save(token);
    }
}
