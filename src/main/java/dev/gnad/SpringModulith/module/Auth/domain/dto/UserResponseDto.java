package dev.gnad.SpringModulith.module.Auth.domain.dto;

import java.time.LocalDateTime;

public record UserResponseDto(
        Integer id,
        String username,
        String email,
        LocalDateTime createdAt,
        LocalDateTime lastModifiedAt
) {
}
