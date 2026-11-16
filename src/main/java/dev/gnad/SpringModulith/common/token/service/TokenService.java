package dev.gnad.SpringModulith.common.token.service;

import dev.gnad.SpringModulith.common.token.domain.entity.BaseToken;
import dev.gnad.SpringModulith.common.token.factory.TokenFactory;
import dev.gnad.SpringModulith.module.Auth.domain.entity.User;

public interface TokenService {
     BaseToken createToken(User user, TokenFactory factory);
     BaseToken validateToken(String rawToken);
     void revoke(String rawToken);
}
