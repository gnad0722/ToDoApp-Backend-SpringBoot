package dev.gnad.SpringModulith.module.Auth.domain.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType
) {
}
