package dev.gnad.SpringModulith.module.Task.policy;

import java.time.LocalDateTime;

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
