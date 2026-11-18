package dev.gnad.SpringModulith.module.Task.domain.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreateTaskDto(

        @Size(min = 5)
        @NotBlank
        String name,

        @NotEmpty
        @NotBlank
        String description,

        @NotNull
        Integer toDoListId,

        LocalDateTime expectStart,

        LocalDateTime expectEnd


) {
}
