package dev.gnad.SpringModulith.module.Task.domain.dto;

import java.util.Set;

public record TaskFilterField() {
    public static final Set<String> TASK_FILTER_FIELDS = Set.of(
            "name",
            "description",
            "isDone",
            "expectStart",
            "expectEnd"
    );
}
