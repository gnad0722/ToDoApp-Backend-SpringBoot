package dev.gnad.SpringModulith.common.policy;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Component
public class TimePolicy {

    public void validateDueDate(LocalDateTime dueDate) {
        if (dueDate != null && dueDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Due date cannot be in the past"
            );
        }
    }

    public void validateExpectedTime(
            LocalDateTime start,
            LocalDateTime end) {

        if (start != null
                && end != null
                && !start.isBefore(end)) {
            throw new IllegalArgumentException(
                    "Expected start time must be before expected end time"
            );
        }
    }
}
