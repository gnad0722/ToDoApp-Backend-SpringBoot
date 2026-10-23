package dev.gnad.SpringModulith.module.Project.domain.dto;

import java.util.Set;

public record ProjectFilter() {
    public static final Set<String> PROJECT_FILTER_FIELDS = Set.of(
            "topic",
            "description",
            "startTime",
            "expectEnd",
            "numTask",
            "numCompleteTask"
    );
}
