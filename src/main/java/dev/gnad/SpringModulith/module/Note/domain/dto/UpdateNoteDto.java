package dev.gnad.SpringModulith.module.Note.domain.dto;

import jakarta.validation.constraints.Size;

public record UpdateNoteDto(
        @Size(min = 10)
        String description
) {
}
