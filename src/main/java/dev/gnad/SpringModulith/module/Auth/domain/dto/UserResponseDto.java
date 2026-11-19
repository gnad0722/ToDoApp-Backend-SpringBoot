package dev.gnad.SpringModulith.module.Auth.domain.dto;

import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

public record UserResponseDto(
        Integer id,
        String username,
        String email,
        LocalDateTime birthday,
        LocalDateTime createdAt,
        LocalDateTime lastModifiedAt
) {
}
