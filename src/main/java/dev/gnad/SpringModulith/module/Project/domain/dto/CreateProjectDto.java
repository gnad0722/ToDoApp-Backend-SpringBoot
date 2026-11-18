package dev.gnad.SpringModulith.module.Project.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreateProjectDto(

        @Size(min=5)
        @NotBlank
        String topic,

        @Size(min = 20)
        @NotBlank
        String  description,

        LocalDateTime startTime,

        LocalDateTime expectEnd

) {
}
