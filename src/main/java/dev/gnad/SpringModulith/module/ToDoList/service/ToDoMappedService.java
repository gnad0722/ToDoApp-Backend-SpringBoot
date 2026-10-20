package dev.gnad.SpringModulith.module.ToDoList.service;

import dev.gnad.SpringModulith.module.Task.domain.dto.UpdateTaskDto;
import dev.gnad.SpringModulith.module.Task.domain.entity.Task;
import dev.gnad.SpringModulith.module.ToDoList.domain.dto.CreateToDoDto;
import dev.gnad.SpringModulith.module.ToDoList.domain.dto.ToDoResponseDto;
import dev.gnad.SpringModulith.module.ToDoList.domain.dto.UpdateToDoDto;
import dev.gnad.SpringModulith.module.ToDoList.domain.entity.ToDoList;
import dev.gnad.SpringModulith.module.Auth.domain.entity.User;
import dev.gnad.SpringModulith.module.Auth.service.AuthService;
import dev.gnad.SpringModulith.module.ToDoList.policy.ToDoPolicyFacade;
import dev.gnad.SpringModulith.module.ToDoList.policy.ToDoTimePolicy;
import org.springframework.stereotype.Service;

@Service
public class ToDoMappedService {

    private final AuthService authService;

    private final ToDoPolicyFacade toDoPolicyFacade;

    public ToDoMappedService(AuthService authService,
                             ToDoPolicyFacade toDoPolicyFacade) {
        this.authService = authService;
        this.toDoPolicyFacade = toDoPolicyFacade;
    }

    public ToDoResponseDto toResponse(ToDoList toDoList ){
        return new ToDoResponseDto(
                toDoList.getId(),
                toDoList.getTopic(),
                toDoList.getStartTime(),
                toDoList.getNumTask(),
                toDoList.getNumCompleteTask(),
                toDoList.getCreatedAt(),
                toDoList.getLastModifiedAt(),
                toDoList.getTaskList());
    }

    public ToDoList toObject (CreateToDoDto createToDoDto, Integer userId){
        ToDoList newList = new ToDoList();
        newList.setTopic(createToDoDto.topic());

        if (createToDoDto.startTime() != null){
            this.toDoPolicyFacade.validateDueDate(createToDoDto.startTime());
            newList.setStartTime(createToDoDto.startTime());
        }

        User user = this.authService.findById(userId);
        newList.setUser(user);
        return newList;
    }

    public void patch(ToDoList toDoList, UpdateToDoDto updateToDoDto){

        if (updateToDoDto.topic() != null){
            toDoList.setTopic(updateToDoDto.topic());
        }

        if (updateToDoDto.startTime() != null){
            this.toDoPolicyFacade.validateDueDate(updateToDoDto.startTime());
            toDoList.setStartTime(updateToDoDto.startTime());
        }
    }
}
