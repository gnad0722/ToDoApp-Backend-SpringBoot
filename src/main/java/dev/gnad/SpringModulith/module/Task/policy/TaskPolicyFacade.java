package dev.gnad.SpringModulith.module.Task.policy;

import dev.gnad.SpringModulith.module.Task.domain.entity.Task;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TaskPolicyFacade {

    private final TaskCompletePolicy taskCompletePolicy;


    public TaskPolicyFacade(TaskCompletePolicy taskCompletePolicy) {
        this.taskCompletePolicy = taskCompletePolicy;
    }


    public void validateCanComplete(Task task) {
        taskCompletePolicy.validateCanComplete(task);
    }


}
