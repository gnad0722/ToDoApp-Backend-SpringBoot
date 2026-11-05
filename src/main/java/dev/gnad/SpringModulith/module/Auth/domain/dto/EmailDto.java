package dev.gnad.SpringModulith.module.Auth.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailDto(
        @NotBlank
        @Email
        String email
) {
}
