package dev.gnad.SpringModulith.module.Task.policy;

import dev.gnad.SpringModulith.common.api.exception.BadRequestException;
import dev.gnad.SpringModulith.module.Task.domain.entity.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskCompletePolicy {

    public void validateCanComplete(Task task) {
        if (task.isDone()) {
            throw new BadRequestException(
                    "Task is already completed"
            );
        }
    }
}
