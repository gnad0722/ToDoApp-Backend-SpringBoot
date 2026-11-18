package dev.gnad.SpringModulith.common.token.service;

import dev.gnad.SpringModulith.common.api.exception.BadRequestException;
import dev.gnad.SpringModulith.common.cache.constants.CacheName;
import dev.gnad.SpringModulith.common.token.domain.entity.BaseToken;
import dev.gnad.SpringModulith.common.token.domain.entity.ResetPasswordToken;
import dev.gnad.SpringModulith.common.token.factory.TokenFactory;
import dev.gnad.SpringModulith.common.token.internal.repository.ResetPasswordTokenRepository;
import dev.gnad.SpringModulith.common.token.utility.TokenCommon;
import dev.gnad.SpringModulith.module.Auth.domain.entity.User;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@CacheConfig(cacheNames = CacheName.ResetToken)
public class ResetPasswordTokenService implements TokenService{

    private static final String INVALID_RESET_TOKEN = "Reset link has expired, please request a new code";

    private final ResetPasswordTokenRepository resetPasswordTokenRepository;

    public ResetPasswordTokenService(ResetPasswordTokenRepository resetPasswordTokenRepository) {
        this.resetPasswordTokenRepository = resetPasswordTokenRepository;
    }

    @Override
//    @CachePut(
//            value = "token",
//            key = "#result.tokenHash"
//    )
    public BaseToken createToken(User user, TokenFactory factory) {
        ResetPasswordToken token = (ResetPasswordToken) factory.createToken(user);
        this.resetPasswordTokenRepository.save(token);
        return token;
    }
//
//    @Cacheable(
//            value = "tokens",
//            keyGenerator = "tokenKeyGenerator"
//    )
    private BaseToken getToken(String rawToken){
        String hashToken = TokenCommon.hashToken(rawToken);
        return this.resetPasswordTokenRepository
                .findByTokenHash(hashToken).orElseThrow(
                        ()->new BadRequestException(INVALID_RESET_TOKEN)
                );
    }

    @Override
    public BaseToken validateToken(String rawToken) {

        BaseToken token = this.getToken(rawToken);

        if (token.isExpired()){
            throw new BadRequestException(INVALID_RESET_TOKEN);
        }

        if (token.isRevoked()){
            throw new BadRequestException(INVALID_RESET_TOKEN);
        }
        return token;
    }

    @Override
    public void revoke(String rawToken) {
        ResetPasswordToken token = (ResetPasswordToken) this.validateToken(rawToken);
        token.setRevokedAt(Instant.now());
        this.resetPasswordTokenRepository.save(token);
    }
}
