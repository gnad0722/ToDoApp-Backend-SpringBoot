package dev.gnad.SpringModulith.common.security.service.implemetation;

import dev.gnad.SpringModulith.common.security.service.facade.AuthenticationTokenService;
import dev.gnad.SpringModulith.common.token.domain.entity.BaseToken;
import dev.gnad.SpringModulith.common.token.factory.RefreshTokenFactory;
import dev.gnad.SpringModulith.common.token.factory.ResetPasswordTokenFactory;
import dev.gnad.SpringModulith.common.token.service.TokenService;
import dev.gnad.SpringModulith.module.Auth.domain.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationTokenServiceIImpl implements AuthenticationTokenService {

    private final AccessTokenService accessTokenService;

    private final TokenService tokenService;

    private final ResetPasswordTokenFactory resetPasswordTokenFactory;

    private final RefreshTokenFactory refreshTokenFactory;

    public AuthenticationTokenServiceIImpl(AccessTokenService accessTokenService,
                                           TokenService tokenService,
                                           ResetPasswordTokenFactory resetPasswordTokenFactory,
                                           RefreshTokenFactory refreshTokenFactory) {
        this.accessTokenService = accessTokenService;
        this.tokenService = tokenService;
        this.resetPasswordTokenFactory = resetPasswordTokenFactory;
        this.refreshTokenFactory = refreshTokenFactory;
    }


    @Override
    public String generateAccessToken(Authentication authentication) {
        return this.accessTokenService.generateAccessToken(authentication);
    }

    @Override
    public void revoke(String rawToken) {
        this.tokenService.revoke(rawToken);
    }

    @Override
    public BaseToken validate(String rawToken) {
        return this.tokenService.validateToken(rawToken);
    }

    @Override
    public String createRefreshToken(User user) {
        BaseToken token = this.tokenService.createToken(user, refreshTokenFactory);
        return token.getTokenHash();
    }

    @Override
    public String createResetPasswordToken(User user) {
        BaseToken token = this.tokenService.createToken(user, resetPasswordTokenFactory);
        return token.getTokenHash();
    }
}
