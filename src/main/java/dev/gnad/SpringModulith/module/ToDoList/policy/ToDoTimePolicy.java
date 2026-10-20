package dev.gnad.SpringModulith.module.ToDoList.policy;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ToDoTimePolicy {

    public void validateDueDate(LocalDateTime dueDate) {
        if (dueDate != null && dueDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Due date cannot be in the past"
            );
        }
    }
}
