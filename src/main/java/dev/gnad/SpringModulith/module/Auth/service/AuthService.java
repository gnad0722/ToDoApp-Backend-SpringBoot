package dev.gnad.SpringModulith.module.Auth.service;

import dev.gnad.SpringModulith.common.api.exception.NotFoundException;
import dev.gnad.SpringModulith.common.security.JwtService;
import dev.gnad.SpringModulith.module.Auth.domain.dto.AuthResponse;
import dev.gnad.SpringModulith.module.Auth.domain.dto.LoginDto;
import dev.gnad.SpringModulith.module.Auth.domain.dto.RegisterDto;
import dev.gnad.SpringModulith.module.Auth.domain.dto.UserResponseDto;
import dev.gnad.SpringModulith.module.Auth.domain.entity.User;
import dev.gnad.SpringModulith.module.Auth.internal.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalLookupService;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final ApplicationEventPublisher publisher;
    private final UserMappedService userMappedService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    public final JwtService jwtService;


    public AuthService(UserRepository userRepository,
                       ApplicationEventPublisher publisher,
                       UserMappedService userMappedService,
                       PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.publisher = publisher;
        this.userMappedService = userMappedService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public UserResponseDto mapToResponse(User user){
        return this.userMappedService.toResponse(user);
    }


    @Transactional
    public User register(RegisterDto registerDto){

            if (userRepository.existsByEmail(registerDto.email())){
                throw new IllegalArgumentException(
                        "Email is already registered."
                );
            }

            String hashedPassword = passwordEncoder.encode(registerDto.password());

            return this.userRepository.save(
                    this.userMappedService.toObject(new RegisterDto(
                            registerDto.username()
                            ,hashedPassword
                            ,registerDto.email()
                    )));

    }

    public AuthResponse login(LoginDto loginDto){
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginDto.email(),
                                loginDto.password()
                        )
                );

        String token = jwtService.generateAccessToken(authentication);
        return new AuthResponse(token,"Bearer");
    }

    public User findById(Integer id){
        return this.userRepository.findById(id)
                .orElseThrow(()->
                new NotFoundException("Not found the user with id: "+id)
        );
    }

}
