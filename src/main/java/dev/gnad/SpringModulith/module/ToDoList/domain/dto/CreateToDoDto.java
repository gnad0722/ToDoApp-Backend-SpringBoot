package dev.gnad.SpringModulith.module.ToDoList.domain.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreateToDoDto(

        @Size(min=10)
        @NotBlank
        String topic,

        @NotNull
        LocalDateTime startTime
){
}
