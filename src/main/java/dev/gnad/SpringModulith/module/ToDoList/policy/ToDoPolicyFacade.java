package dev.gnad.SpringModulith.module.ToDoList.policy;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ToDoPolicyFacade {

    private final ToDoTimePolicy toDoTimePolicy;


    public ToDoPolicyFacade(ToDoTimePolicy toDoTimePolicy) {
        this.toDoTimePolicy = toDoTimePolicy;
    }

    public void validateDueDate(LocalDateTime dueDate){
        this.toDoTimePolicy.validateDueDate(dueDate);
    }
}
