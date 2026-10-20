package dev.gnad.SpringModulith.module.Auth.controller;

import dev.gnad.SpringModulith.common.api.response.ApiResponse;
import dev.gnad.SpringModulith.common.provider.CurrentUserProvider;
import dev.gnad.SpringModulith.module.Auth.domain.dto.AuthResponse;
import dev.gnad.SpringModulith.module.Auth.domain.dto.LoginDto;
import dev.gnad.SpringModulith.module.Auth.domain.dto.RegisterDto;
import dev.gnad.SpringModulith.module.Auth.domain.dto.UserResponseDto;
import dev.gnad.SpringModulith.module.Auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDto>> getMe(@AuthenticationPrincipal Jwt jwt){

        UserResponseDto response = this.authService.mapToResponse(
                this.authService.findById(CurrentUserProvider.getUserId(jwt))
        );
        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Get my profile successfully"
                )
        );
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDto>> register(@RequestBody @Valid RegisterDto registerDto){
        UserResponseDto response = this.authService.mapToResponse(
                this.authService.register(registerDto));

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
}
