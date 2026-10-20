package dev.gnad.SpringModulith.module.Auth.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterDto(
        @Size(min = 4)
        @NotBlank
        String username,

        @Size(min=6, max = 72)
        @NotBlank
        String password,

        @Email
        @NotBlank
        String email



) {
}
