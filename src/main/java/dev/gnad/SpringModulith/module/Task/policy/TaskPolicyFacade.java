package dev.gnad.SpringModulith.module.Task.policy;

import dev.gnad.SpringModulith.module.Task.domain.entity.Task;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TaskPolicyFacade {

    private final TaskCompletePolicy taskCompletePolicy;
    private final TaskTimePolicy taskTimePolicy;


    public TaskPolicyFacade(TaskCompletePolicy taskCompletePolicy,
                            TaskTimePolicy taskTimePolicy) {
        this.taskCompletePolicy = taskCompletePolicy;
        this.taskTimePolicy = taskTimePolicy;
    }


    public void validateCanComplete(Task task) {
        taskCompletePolicy.validateCanComplete(task);
    }

    public void validateStartTime(
            LocalDateTime startTask,
            LocalDateTime startToDo) {
        taskTimePolicy.validateStartTime(startTask,startToDo);
    }

    }
