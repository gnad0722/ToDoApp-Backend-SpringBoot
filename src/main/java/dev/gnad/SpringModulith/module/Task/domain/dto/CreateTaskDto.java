package dev.gnad.SpringModulith.module.Task.domain.dto;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreateTaskDto(

        @Size(min = 5)
        String name,

        @NotEmpty
        String description,

        @NotEmpty
        Integer toDoListId,

        LocalDateTime expectStart,

        LocalDateTime expectEnd


) {
}
