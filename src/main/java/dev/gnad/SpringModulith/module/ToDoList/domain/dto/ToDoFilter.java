package dev.gnad.SpringModulith.module.ToDoList.domain.dto;

import java.util.Set;

public record ToDoFilter() {
    public static final Set<String> To_Do_FILTER_FIELDS = Set.of(
            "topic",
            "startTime",
            "numTask",
            "numCompleteTask"
    );
}
