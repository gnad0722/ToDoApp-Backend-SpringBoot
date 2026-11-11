package dev.gnad.SpringModulith.common.security.service.facade;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public interface SecurityService {
    String hashPassword(String password);

    Authentication authentication(UsernamePasswordAuthenticationToken token);
}
