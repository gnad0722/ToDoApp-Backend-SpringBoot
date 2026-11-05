package dev.gnad.SpringModulith.module.Auth.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordDto(
        @Size(min=6, max = 72)
        @NotBlank
        String newPassword,

        @NotBlank
        String resetPasswordToken
) {
}
