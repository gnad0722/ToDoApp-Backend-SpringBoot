package dev.gnad.SpringModulith.common.token.factory;

import dev.gnad.SpringModulith.common.token.domain.entity.BaseToken;
import dev.gnad.SpringModulith.common.token.domain.entity.ResetPasswordToken;
import dev.gnad.SpringModulith.common.token.strategy.ResetPasswordTokenStrategy;
import dev.gnad.SpringModulith.common.token.utility.TokenCommon;
import dev.gnad.SpringModulith.module.Auth.domain.entity.User;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@Component
public class ResetPasswordTokenFactory implements TokenFactory{

    private final SecureRandom secureRandom = new SecureRandom();

    private final ResetPasswordTokenStrategy resetPasswordTokenStrategy;

    public ResetPasswordTokenFactory(ResetPasswordTokenStrategy resetPasswordTokenStrategy) {
        this.resetPasswordTokenStrategy = resetPasswordTokenStrategy;
    }

    private String generateResetToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }


    @Override
    public BaseToken createToken(User user) {
        String rawToken = this.generateResetToken();

        ResetPasswordToken resetPasswordToken = new ResetPasswordToken();

        resetPasswordToken.setTokenRaw(rawToken);
        resetPasswordToken.setTokenHash(TokenCommon.hashToken(rawToken));
        resetPasswordToken.setUser(user);
        resetPasswordToken.setExpireAt(
                Instant.now().plusMillis(this.resetPasswordTokenStrategy.getExpiration())
        );
        return resetPasswordToken;
    }


}
