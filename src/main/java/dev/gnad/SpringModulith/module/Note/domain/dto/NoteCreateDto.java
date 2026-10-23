package dev.gnad.SpringModulith.module.Note.domain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NoteCreateDto(
        @Size(min = 10)
        String description,

        @NotNull
        Integer taskId
) {
}
