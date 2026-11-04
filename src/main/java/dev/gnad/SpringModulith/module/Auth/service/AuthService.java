package dev.gnad.SpringModulith.module.Auth.service;

import dev.gnad.SpringModulith.common.api.exception.NotFoundException;
import dev.gnad.SpringModulith.common.security.JwtService;
import dev.gnad.SpringModulith.module.Auth.domain.dto.AuthResponse;
import dev.gnad.SpringModulith.module.Auth.domain.dto.LoginDto;
import dev.gnad.SpringModulith.module.Auth.domain.dto.RegisterDto;
import dev.gnad.SpringModulith.module.Auth.domain.dto.UserResponseDto;
import dev.gnad.SpringModulith.module.Auth.domain.entity.RefreshToken;
import dev.gnad.SpringModulith.module.Auth.domain.entity.User;
import dev.gnad.SpringModulith.module.Auth.internal.repository.UserRepository;
import dev.gnad.SpringModulith.module.Auth.security.UserPrincipal;
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
    public final RefreshTokenService refreshTokenService;


    public AuthService(UserRepository userRepository,
                       ApplicationEventPublisher publisher,
                       UserMappedService userMappedService,
                       PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.publisher = publisher;
        this.userMappedService = userMappedService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
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

        String accessToken = jwtService.generateAccessToken(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        String refreshToken = this.refreshTokenService.createRefreshToken(userPrincipal.getUser());


        return new AuthResponse(accessToken, refreshToken,"Bearer");
    }


    @Transactional
    public void logout(String rawRefreshToken) {

        RefreshToken refreshToken =
                refreshTokenService.validateRefreshToken(rawRefreshToken);

        refreshTokenService.revokedToken(refreshToken);
    }

    public User findById(Integer id){
        return this.userRepository.findById(id)
                .orElseThrow(()->
                new NotFoundException("Not found the user with id: "+id)
        );
    }


    @Transactional
    public AuthResponse refresh(String rawRefreshToken){

        RefreshToken oldToken  = this.refreshTokenService.validateRefreshToken(rawRefreshToken);

        User user = oldToken.getUser();


        this.refreshTokenService.revokedToken(oldToken);

        UserPrincipal userPrincipal = new UserPrincipal(user);

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userPrincipal,
                        null,
                        userPrincipal.getAuthorities()
                );


        String accessToken = this.jwtService.generateAccessToken(authentication);

        String refreshToken = this.refreshTokenService.createRefreshToken(user);


        return new AuthResponse(accessToken, refreshToken, "Bearer");
    }

}
