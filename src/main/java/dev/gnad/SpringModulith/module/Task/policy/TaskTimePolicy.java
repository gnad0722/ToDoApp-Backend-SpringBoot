package dev.gnad.SpringModulith.module.Task.policy;

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
            throw new IllegalArgumentException(
                    "Start time of task must be after start time of To Do List."
            );
        }
    }
}
