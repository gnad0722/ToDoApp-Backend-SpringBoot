package dev.gnad.SpringModulith.module.Task.policy;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TaskTimePolicy {

    public void validateDueDate(LocalDateTime dueDate) {
        if (dueDate != null && dueDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Due date cannot be in the past"
            );
        }
    }

    public void validateExpectedTime(
            LocalDateTime expectedStart,
            LocalDateTime expectedEnd) {

        if (expectedStart != null
                && expectedEnd != null
                && !expectedStart.isBefore(expectedEnd)) {
            throw new IllegalArgumentException(
                    "Expected start time must be before expected end time"
            );
        }
    }
}
