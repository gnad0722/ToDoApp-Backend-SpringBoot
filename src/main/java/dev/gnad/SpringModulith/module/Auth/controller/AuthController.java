package dev.gnad.SpringModulith.module.Auth.controller;

import dev.gnad.SpringModulith.common.api.response.ApiResponse;
import dev.gnad.SpringModulith.common.provider.CurrentUserProvider;
import dev.gnad.SpringModulith.module.Auth.domain.dto.*;
import dev.gnad.SpringModulith.module.Auth.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.antlr.v4.runtime.Token;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(
        name = "Authentication",
        description = "APIs for authentication"
)
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDto>> getMe(@AuthenticationPrincipal Jwt jwt){

        UserResponseDto response = this.authService.findById(CurrentUserProvider.getUserId(jwt));
        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Get my profile successfully"
                )
        );
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDto>> register(@RequestBody @Valid RegisterDto registerDto){
        UserResponseDto response = this.authService.register(registerDto);

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Registered successfully")
        );
    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody @Valid LoginDto loginDto){

        AuthResponse response = this.authService.login(loginDto);

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Login successfully")
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestBody @Valid RefreshTokenDto refreshTokenDto){
        this.authService.logout(refreshTokenDto.rawToken());
        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        "Logout successfully")
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @RequestBody @Valid RefreshTokenDto refreshTokenDto
    ){

        AuthResponse response = this.authService.refresh(refreshTokenDto.rawToken());

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Refresh Token Successfully"
                )
        );
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> sendOtpToEmail(
            @RequestBody @Valid EmailDto emailDto
    ){
        this.authService.sendOtpToEmail(emailDto);
        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        "OTP is sent to email successfully"
                )
        );
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<TokenDto>> verifyOtp(
            @RequestBody @Valid VerifyOtpDto verifyOtpDto
    ){
        TokenDto response = this.authService.validatOtp(verifyOtpDto);

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "OTP is sent to email successfully"
                )
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<UserResponseDto>> resetPassword(
            @RequestBody @Valid ResetPasswordDto resetPasswordDto
    ){
        UserResponseDto response = this.authService.updatePassword(resetPasswordDto);

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Reset Password Successfully!"
                )
        );
    }

    @PatchMapping("/update-profile")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateProfile(
            @RequestBody @Valid UpdateUserDto updateUserDto,
            @AuthenticationPrincipal Jwt jwt
    ){
        UserResponseDto response = this.authService.updateProfile(CurrentUserProvider.getUserId(jwt),updateUserDto);

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Update Profile Successfully"
                )
        );
    }
}
