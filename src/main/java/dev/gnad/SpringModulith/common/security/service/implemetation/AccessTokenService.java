package dev.gnad.SpringModulith.common.security.service.implemetation;


import dev.gnad.SpringModulith.module.Auth.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class AccessTokenService {

    private final JwtEncoder jwtEncoder;
    private final long accessTokenMinutes;
    private final String issuer;

    public AccessTokenService(JwtEncoder jwtEncoder,
                              @Value("${security.jwt.issuer}")
                      String issuer,
                              @Value("${security.jwt.access-token-minutes}")
                      long accessTokenMinutes
                     )
    {
        this.jwtEncoder = jwtEncoder;
        this.accessTokenMinutes = accessTokenMinutes;
        this.issuer = issuer;
    }

    public String generateAccessToken(
            Authentication authentication
    ){
        Instant now = Instant.now();

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority).toList();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(accessTokenMinutes * 60))
                .subject(authentication.getName())
                .claim("roles", roles)
                .claim("userId", userPrincipal.getUserId())
                .build();
        JwsHeader header = JwsHeader
                .with(MacAlgorithm.HS256)
                .build();

        return jwtEncoder.encode(
                JwtEncoderParameters.from(header,claims)
        ).getTokenValue();
    }

}
