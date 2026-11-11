package dev.gnad.SpringModulith.common.security.service.facade;

import dev.gnad.SpringModulith.common.token.domain.entity.BaseToken;
import dev.gnad.SpringModulith.module.Auth.domain.entity.User;
import org.springframework.security.core.Authentication;

public interface AuthenticationTokenService {
    String generateAccessToken(Authentication authentication);
    void revoke(String rawToken);
    BaseToken validate(String rawToken);
    String createRefreshToken(User user);
    String createResetPasswordToken(User user);
}
