package dev.gnad.SpringModulith.module.Auth.service;

import dev.gnad.SpringModulith.module.Auth.internal.repository.UserRepository;
import dev.gnad.SpringModulith.module.Auth.security.UserPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomerUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomerUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        return this.userRepository
                .findByEmail(email)
                .map(UserPrincipal::new)
                .orElseThrow(()->
                    new UsernameNotFoundException("Cannot find user with email: "+email));
    }
}
