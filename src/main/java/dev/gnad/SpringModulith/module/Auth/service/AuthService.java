package dev.gnad.SpringModulith.module.Auth.service;

import dev.gnad.SpringModulith.common.api.exception.NotFoundException;
import dev.gnad.SpringModulith.common.email.EmailSenderService;
import dev.gnad.SpringModulith.common.otp.domain.entity.Otp;
import dev.gnad.SpringModulith.common.otp.service.OtpService;
import dev.gnad.SpringModulith.common.security.service.facade.AuthenticationTokenService;
import dev.gnad.SpringModulith.common.security.service.facade.SecurityService;
import dev.gnad.SpringModulith.common.token.domain.entity.BaseToken;
import dev.gnad.SpringModulith.module.Auth.domain.dto.*;
import dev.gnad.SpringModulith.module.Auth.domain.entity.User;
import dev.gnad.SpringModulith.module.Auth.internal.repository.UserRepository;
import dev.gnad.SpringModulith.module.Auth.security.UserPrincipal;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final ApplicationEventPublisher publisher;
    private final UserMappedService userMappedService;
    private final OtpService otpService;
    private final EmailSenderService emailSenderService;
    private final SecurityService securityService;
    private final AuthenticationTokenService authenticationTokenService;


    public AuthService(UserRepository userRepository,
                       ApplicationEventPublisher publisher,
                       UserMappedService userMappedService,
                       OtpService otpService,
                       EmailSenderService emailSenderService4,
                       SecurityService securityService,
                       AuthenticationTokenService authenticationTokenService) {
        this.userRepository = userRepository;
        this.publisher = publisher;
        this.userMappedService = userMappedService;
        this.otpService = otpService;
        this.emailSenderService = emailSenderService4;
        this.securityService = securityService;
        this.authenticationTokenService = authenticationTokenService;
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

            String hashedPassword = this.securityService.hashPassword(registerDto.password());

            return this.userRepository.save(
                    this.userMappedService.toObject(new RegisterDto(
                            registerDto.username()
                            ,hashedPassword
                            ,registerDto.email()
                    )));

    }

    public AuthResponse login(LoginDto loginDto){
        Authentication authentication =
               this.securityService.authentication( new UsernamePasswordAuthenticationToken(
                       loginDto.email(),
                       loginDto.password()
               ));

        String accessToken = this.authenticationTokenService.generateAccessToken(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        String refreshToken = this.authenticationTokenService.createRefreshToken(userPrincipal.getUser());


        return new AuthResponse(accessToken, refreshToken,"Bearer");
    }


    @Transactional
    public void logout(String rawRefreshToken) {

        this.authenticationTokenService.revoke(rawRefreshToken);
    }



    @Transactional
    public void sendOtpToEmail(EmailDto emailDto){
        User user = this.userRepository.findByEmail(emailDto.email())
                .orElseThrow(()->
                new NotFoundException("Not found the user with email: "+emailDto.email())
                );

        String otp = this.otpService.createOtp(user);

        this.emailSenderService.sendOtp(user.getEmail(), otp);
    }

    public TokenDto validatOtp(VerifyOtpDto verifyOtpDto){
        Otp validatedOtp = this.otpService.validateOtp(verifyOtpDto.otp(), verifyOtpDto.email());



        String token = this.authenticationTokenService.createResetPasswordToken(validatedOtp.getUser());


        return new TokenDto(token);
    }

    @Transactional
    public User updatePassword(ResetPasswordDto resetPasswordDto){

        BaseToken resetPasswordToken = this.authenticationTokenService.validate(
                resetPasswordDto.resetPasswordToken());

        User user = resetPasswordToken.getUser();

        user.setPassword(resetPasswordDto.newPassword());

        this.userRepository.save(user);

        this.authenticationTokenService.revoke(resetPasswordDto.resetPasswordToken());

        return user;
    }

    public User findById(Integer id){
        return this.userRepository.findById(id)
                .orElseThrow(()->
                new NotFoundException("Not found the user with id: "+id)
        );
    }


    @Transactional
    public AuthResponse refresh(String rawRefreshToken){

        BaseToken oldToken  = this.authenticationTokenService.validate(rawRefreshToken);

        User user = oldToken.getUser();


        this.authenticationTokenService.revoke(rawRefreshToken);

        UserPrincipal userPrincipal = new UserPrincipal(user);

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userPrincipal,
                        null,
                        userPrincipal.getAuthorities()
                );


        String accessToken = this.authenticationTokenService.generateAccessToken(authentication);

        String refreshToken = this.authenticationTokenService.createRefreshToken(user);


        return new AuthResponse(accessToken, refreshToken, "Bearer");
    }

}
