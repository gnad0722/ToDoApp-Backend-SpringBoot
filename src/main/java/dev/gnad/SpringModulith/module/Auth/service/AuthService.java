package dev.gnad.SpringModulith.module.Auth.service;

import dev.gnad.SpringModulith.common.api.exception.ConflictException;
import dev.gnad.SpringModulith.common.api.exception.NotFoundException;
import dev.gnad.SpringModulith.common.email.EmailSenderService;
import dev.gnad.SpringModulith.common.email.utility.EmailGenerator;
import dev.gnad.SpringModulith.common.otp.domain.entity.Otp;
import dev.gnad.SpringModulith.common.otp.service.OtpService;
import dev.gnad.SpringModulith.common.policy.PolicyFacade;
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
                       AuthenticationTokenService authenticationTokenService
                       ) {
        this.userRepository = userRepository;
        this.publisher = publisher;
        this.userMappedService = userMappedService;
        this.otpService = otpService;
        this.emailSenderService = emailSenderService4;
        this.securityService = securityService;
        this.authenticationTokenService = authenticationTokenService;
    }

    @Transactional
    public UserResponseDto register(RegisterDto registerDto){


            if (userRepository.existsByEmail(registerDto.email())){
                throw new ConflictException("Email is already registered");
            }


            String hashedPassword = this.securityService.hashPassword(registerDto.password());

            User user = this.userRepository.save(
                    this.userMappedService.toObject(new RegisterDto(
                            registerDto.username()
                            ,hashedPassword
                            ,registerDto.email(),
                            registerDto.birthday()
                    )));

            return this.userMappedService.toResponse(user);

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
        this.authenticationTokenService.revokeRefreshToken(rawRefreshToken);
    }



    @Transactional
    public void sendOtpToEmail(EmailDto emailDto){
        User user = this.userRepository.findByEmail(emailDto.email())
                .orElseThrow(()->
                new NotFoundException("No account found with this email")
                );

        String otp = this.otpService.createOtp(user);

        this.emailSenderService.sendEmail(
                user.getEmail(),
                EmailGenerator.generateOtpEmail(otp),
                "Password Reset OTP"
        );
    }

    public TokenDto validatOtp(VerifyOtpDto verifyOtpDto){
        Otp validatedOtp = this.otpService.validateOtp(verifyOtpDto.otp(), verifyOtpDto.email());



        String token = this.authenticationTokenService.createResetPasswordToken(validatedOtp.getUser());


        return new TokenDto(token);
    }

    @Transactional
    public UserResponseDto updatePassword(ResetPasswordDto resetPasswordDto){

        BaseToken resetPasswordToken = this.authenticationTokenService.validateResetToken(
                resetPasswordDto.resetPasswordToken());

        User user = resetPasswordToken.getUser();

        String newPasswordHashed = this.securityService.hashPassword(resetPasswordDto.newPassword());
        user.setPassword(newPasswordHashed);

        this.userRepository.save(user);
        this.authenticationTokenService.revokeResetToken(resetPasswordDto.resetPasswordToken());

        return this.userMappedService.toResponse(user);
    }

    /**
     * Returns the managed entity. Use this from other modules when an entity
     * is needed to build a relation, e.g. ToDoList -> User.
     */
    public User getReference(Integer id){
        return this.userRepository.findById(id)
                .orElseThrow(()->
                new NotFoundException("User not found")
        );
    }

    public UserResponseDto findById(Integer id){
        return this.userMappedService.toResponse(this.getReference(id));
    }


    @Transactional
    public AuthResponse refresh(String rawRefreshToken){

        BaseToken oldToken  = this.authenticationTokenService.validateRefreshToken(rawRefreshToken);

        User user = oldToken.getUser();


        this.authenticationTokenService.revokeRefreshToken(rawRefreshToken);

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


    @Transactional
    public UserResponseDto updateProfile(Integer id, UpdateUserDto updateUserDto){
        User user = this.userRepository.findById(id).orElseThrow(
                ()->new NotFoundException("User Not Found")
        );

        this.userMappedService.patch(updateUserDto,user);
        this.userRepository.save(user);
        return this.userMappedService.toResponse(user);
    }
}
