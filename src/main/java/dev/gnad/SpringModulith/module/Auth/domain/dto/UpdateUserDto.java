package dev.gnad.SpringModulith.module.Auth.domain.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record UpdateUserDto(
        @Size(min = 4)
        String username,

        LocalDateTime birthday
) {
}
