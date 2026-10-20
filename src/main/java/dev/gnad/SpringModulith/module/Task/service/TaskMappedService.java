package dev.gnad.SpringModulith.module.Task.service;

import dev.gnad.SpringModulith.module.Task.domain.dto.CreateTaskDto;
import dev.gnad.SpringModulith.module.Task.domain.dto.UpdateTaskDto;
import dev.gnad.SpringModulith.module.Task.domain.dto.TaskDto;
import dev.gnad.SpringModulith.module.Task.domain.entity.Task;
import dev.gnad.SpringModulith.module.Task.policy.TaskPolicyFacade;
import dev.gnad.SpringModulith.module.ToDoList.domain.entity.ToDoList;
import dev.gnad.SpringModulith.module.ToDoList.service.ToDoService;
import org.springframework.stereotype.Service;

@Service
public class TaskMappedService {

    public final TaskPolicyFacade taskPolicyFacade;

    public TaskMappedService(TaskPolicyFacade taskPolicyFacade) {
        this.taskPolicyFacade = taskPolicyFacade;
    }

    public Task toObject(CreateTaskDto createTaskDto, ToDoList toDoList){
        Task newTask = new Task();
        newTask.setDescription(createTaskDto.description());
        newTask.setName(createTaskDto.name());
        newTask.setToDoList(toDoList);

        if (createTaskDto.expectStart() != null){

            this.taskPolicyFacade.validateDueDate(createTaskDto.expectStart());
            newTask.setExpectStart(createTaskDto.expectStart());

            if (createTaskDto.expectEnd() != null){
                this.taskPolicyFacade.validateExpectTime(
                        createTaskDto.expectStart(),
                        createTaskDto.expectEnd());
                newTask.setExpectEnd(createTaskDto.expectEnd());
            }
        }


        return newTask;
    }
    public TaskDto toResponse(Task task){
        return new TaskDto(
                task.getId(),
                task.getName(),
                task.getDescription(),
                task.isDone(),
                task.getExpectStart(),
                task.getExpectEnd(),
                task.getCreatedAt(),
                task.getLastModifiedAt(),
                task.getToDoList().getId());
    }

    public void patch(Task task, UpdateTaskDto updateTaskDto){
        if (updateTaskDto.description() != null){
            task.setDescription(updateTaskDto.description());
        }

        if (updateTaskDto.name() != null) {
            task.setName(updateTaskDto.name());
        }


        if (updateTaskDto.expectStart() != null){
            this.taskPolicyFacade.validateDueDate(updateTaskDto.expectStart());
            task.setExpectStart(updateTaskDto.expectStart());
            if (updateTaskDto.expectEnd() != null){
                this.taskPolicyFacade.validateExpectTime(
                        updateTaskDto.expectStart(),
                        updateTaskDto.expectEnd());
                task.setExpectEnd(updateTaskDto.expectEnd());
            }
        }
        else if (updateTaskDto.expectEnd() != null){
            this.taskPolicyFacade.validateExpectTime(task.getExpectStart(), updateTaskDto.expectEnd());
            task.setExpectEnd(updateTaskDto.expectEnd());
        }
    }
}
