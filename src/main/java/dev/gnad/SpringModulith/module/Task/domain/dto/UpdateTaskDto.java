package dev.gnad.SpringModulith.module.Task.domain.dto;

import java.time.LocalDateTime;

public record UpdateTaskDto(

        String name,

        String description,

        LocalDateTime expectStart,

        LocalDateTime expectEnd
) { }
