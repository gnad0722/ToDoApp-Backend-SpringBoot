package dev.gnad.SpringModulith.module.ToDoList.domain.dto;

import java.time.LocalDateTime;

public record UpdateToDoDto(
        String topic,
        LocalDateTime startTime
) {
}
