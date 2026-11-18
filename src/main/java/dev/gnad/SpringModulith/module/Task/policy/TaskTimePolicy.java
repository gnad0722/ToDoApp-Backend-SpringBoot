package dev.gnad.SpringModulith.module.Task.policy;

import dev.gnad.SpringModulith.common.api.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TaskTimePolicy {
    public void validateStartTime(
            LocalDateTime startTask,
            LocalDateTime startToDo) {

        if (startTask != null
                && startToDo != null
                && !startToDo.isBefore(startTask)) {
            throw new BadRequestException(
                    "Task start time must be after the to-do list start time"
            );
        }
    }
}
