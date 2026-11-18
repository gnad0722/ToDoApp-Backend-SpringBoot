package dev.gnad.SpringModulith.common.policy;

import dev.gnad.SpringModulith.common.api.exception.BadRequestException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Component
public class TimePolicy {

    public void validateDueDate(LocalDateTime dueDate) {
        if (dueDate != null && dueDate.isBefore(LocalDateTime.now())) {
            throw new BadRequestException(
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
            throw new BadRequestException(
                    "End time must be after start time"
            );
        }
    }
}
