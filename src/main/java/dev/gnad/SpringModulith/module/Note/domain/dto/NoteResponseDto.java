package dev.gnad.SpringModulith.module.Note.domain.dto;

import java.time.LocalDateTime;

public record NoteResponseDto(
        Integer id,
        String description,
        LocalDateTime createdAt,
        LocalDateTime lastModifiedAt,
        Integer taskId
) {
}
