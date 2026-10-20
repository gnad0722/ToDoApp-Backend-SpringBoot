package dev.gnad.SpringModulith.common.provider;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {

    public static Integer getUserId(Jwt jwt){
        Number userId = jwt.getClaim("userId");

        if (userId == null){
            throw new AccessDeniedException(
                    "JWT does not contain a userId claim"
            );
        }

        return userId.intValue();
    }
}
