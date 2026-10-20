package dev.gnad.SpringModulith.module.ToDoList.domain.dto;

import dev.gnad.SpringModulith.module.Task.domain.entity.Task;

import java.time.LocalDateTime;
import java.util.List;

public record ToDoResponseDto(
    Integer id,
    String topic,
    LocalDateTime startTime,
    Integer numTask,
    Integer numCompleteTask,
    LocalDateTime createdAt,
    LocalDateTime lastModifiedAt,
    List<Task> taskList
) {
}
