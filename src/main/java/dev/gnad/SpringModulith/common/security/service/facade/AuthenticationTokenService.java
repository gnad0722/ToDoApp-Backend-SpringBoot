package dev.gnad.SpringModulith.common.security.service.facade;

import dev.gnad.SpringModulith.common.token.domain.entity.BaseToken;
import dev.gnad.SpringModulith.module.Auth.domain.entity.User;
import org.springframework.security.core.Authentication;

public interface AuthenticationTokenService {
    String generateAccessToken(Authentication authentication);
    void revokeRefreshToken(String rawToken);
    void revokeResetToken(String rawToken);
    BaseToken validateRefreshToken(String rawToken);
    BaseToken validateResetToken(String rawToken);
    String createRefreshToken(User user);
    String createResetPasswordToken(User user);
}
