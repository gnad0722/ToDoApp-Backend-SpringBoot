package dev.gnad.SpringModulith.common.provider;

import dev.gnad.SpringModulith.common.api.exception.UnauthorizedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {

    public static Integer getUserId(Jwt jwt){
        Number userId = jwt.getClaim("userId");

        if (userId == null){
            throw new UnauthorizedException(
                    "Unauthorized"
            );
        }

        return userId.intValue();
    }
}
