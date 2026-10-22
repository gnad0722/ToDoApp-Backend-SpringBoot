package dev.gnad.SpringModulith.module.Project.domain.dto;

import java.time.LocalDateTime;

public record UpdateProjectDto(
        String topic,
        String desription,
        LocalDateTime startTime,
        LocalDateTime expectEnd
) {
}
