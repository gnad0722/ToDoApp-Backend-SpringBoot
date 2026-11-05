package dev.gnad.SpringModulith.module.Auth.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record VerifyOtpDto(
        @NotBlank
        @Email
        String email,

        @NotBlank
        String otp
) {
}
