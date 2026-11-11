package dev.gnad.SpringModulith.common.token.strategy;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ResetPasswordTokenStrategy implements TokenStrategy{

    @Value("${security.jwt.reset-password-token-expiration}")
    private long expiration;
    @Override
    public long getExpiration() {
        return expiration;
    }
}
