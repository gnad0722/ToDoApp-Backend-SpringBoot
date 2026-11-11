package dev.gnad.SpringModulith.common.security.service.implemetation;

import dev.gnad.SpringModulith.common.security.service.facade.SecurityService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SecurityServiceImpl implements SecurityService {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public SecurityServiceImpl(PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public String hashPassword(String password) {
        return this.passwordEncoder.encode(password);
    }

    @Override
    public Authentication authentication(UsernamePasswordAuthenticationToken token) {
        return this.authenticationManager.authenticate(
                        token
                );
    }
}
