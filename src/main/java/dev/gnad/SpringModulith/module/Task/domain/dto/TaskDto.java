package dev.gnad.SpringModulith.module.Task.domain.dto;

import dev.gnad.SpringModulith.module.Note.domain.entity.Note;

import java.time.LocalDateTime;
import java.util.Iterator;

public record TaskDto(
        Integer id,
        String name,
        String desc,
        boolean isDone,
        LocalDateTime expectStart,
        LocalDateTime expectEnd,
        LocalDateTime createdAt,
        LocalDateTime lastModifiedAt,
        Integer toDoId,
        Note note)
{
}
