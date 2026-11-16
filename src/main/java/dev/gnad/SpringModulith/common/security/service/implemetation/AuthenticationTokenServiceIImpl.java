package dev.gnad.SpringModulith.common.security.service.implemetation;

import dev.gnad.SpringModulith.common.security.service.facade.AuthenticationTokenService;
import dev.gnad.SpringModulith.common.token.domain.entity.BaseToken;
import dev.gnad.SpringModulith.common.token.factory.RefreshTokenFactory;
import dev.gnad.SpringModulith.common.token.factory.ResetPasswordTokenFactory;
import dev.gnad.SpringModulith.common.token.service.RefreshTokenService;
import dev.gnad.SpringModulith.common.token.service.ResetPasswordTokenService;
import dev.gnad.SpringModulith.module.Auth.domain.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationTokenServiceIImpl implements AuthenticationTokenService {

    private final AccessTokenService accessTokenService;

    private final RefreshTokenService refreshTokenService;

    private final ResetPasswordTokenService resetPasswordTokenService;

    private final ResetPasswordTokenFactory resetPasswordTokenFactory;

    private final RefreshTokenFactory refreshTokenFactory;

    public AuthenticationTokenServiceIImpl(AccessTokenService accessTokenService,
                                           RefreshTokenService refreshTokenService,
                                           ResetPasswordTokenService resetPasswordTokenService,
                                           ResetPasswordTokenFactory resetPasswordTokenFactory,
                                           RefreshTokenFactory refreshTokenFactory) {
        this.accessTokenService = accessTokenService;
        this.refreshTokenService = refreshTokenService;
        this.resetPasswordTokenService = resetPasswordTokenService;
        this.resetPasswordTokenFactory = resetPasswordTokenFactory;
        this.refreshTokenFactory = refreshTokenFactory;
    }


    @Override
    public String generateAccessToken(Authentication authentication) {
        return this.accessTokenService.generateAccessToken(authentication);
    }

    @Override
    public void revokeRefreshToken(String rawToken) {
        this.refreshTokenService.revoke(rawToken);
    }

    @Override
    public void revokeResetToken(String rawToken) {
        this.resetPasswordTokenService.revoke(rawToken);
    }

    @Override
    public BaseToken validateRefreshToken(String rawToken) {
        return this.refreshTokenService.validateToken(rawToken);
    }

    @Override
    public BaseToken validateResetToken(String rawToken) {
        return this.resetPasswordTokenService.validateToken(rawToken);
    }

    @Override
    public String createRefreshToken(User user) {
        BaseToken token = this.refreshTokenService.createToken(user, refreshTokenFactory);
        return token.getTokenRaw();
    }

    @Override
    public String createResetPasswordToken(User user) {
        BaseToken token = this.resetPasswordTokenService.createToken(user, resetPasswordTokenFactory);
        return token.getTokenRaw();
    }
}
