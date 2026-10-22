package dev.gnad.SpringModulith.module.Project.domain.dto;

import dev.gnad.SpringModulith.module.Task.domain.entity.Task;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.List;

public record ProjectResponseDto(
        Integer id,
        String topic,
        String description,
        LocalDateTime startTime,
        LocalDateTime expectEnd,
        Integer numTask,
        Integer numCompleteTask,
        LocalDateTime createdAt,
        LocalDateTime lastModifiedAt,
        List<Task> taskList
) {
}
