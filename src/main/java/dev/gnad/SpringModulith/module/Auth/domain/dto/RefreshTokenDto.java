package dev.gnad.SpringModulith.module.Auth.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenDto(
        @NotBlank String rawToken
) {
}
