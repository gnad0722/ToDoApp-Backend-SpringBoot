package dev.gnad.SpringModulith.common.token.factory;

import dev.gnad.SpringModulith.common.token.domain.entity.BaseToken;
import dev.gnad.SpringModulith.module.Auth.domain.entity.User;

public interface TokenFactory {
    BaseToken createToken(User user);
}
