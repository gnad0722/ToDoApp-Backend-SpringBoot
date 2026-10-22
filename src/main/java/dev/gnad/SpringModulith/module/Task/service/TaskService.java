package dev.gnad.SpringModulith.module.Task.service;

import dev.gnad.SpringModulith.common.api.exception.NotFoundException;
import dev.gnad.SpringModulith.module.Task.domain.dto.CreateTaskDto;
import dev.gnad.SpringModulith.module.Task.domain.dto.TaskFilterDto;
import dev.gnad.SpringModulith.module.Task.domain.dto.UpdateTaskDto;
import dev.gnad.SpringModulith.module.Task.domain.dto.TaskDto;
import dev.gnad.SpringModulith.module.Task.domain.entity.Task;
import dev.gnad.SpringModulith.module.Task.internal.repository.TaskRepository;
import dev.gnad.SpringModulith.module.Task.policy.TaskPolicyFacade;
import dev.gnad.SpringModulith.module.ToDoList.domain.entity.ToDoList;
import dev.gnad.SpringModulith.module.ToDoList.service.ToDoService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ApplicationEventPublisher publisher;
    private final TaskMappedService taskMappedService;
    private final ToDoService toDoService;
    private final TaskPolicyFacade taskPolicyFacade;

    public TaskService(TaskRepository taskRepository,
                       ApplicationEventPublisher publisher,
                       TaskMappedService taskMappedService,
                       ToDoService toDoService, TaskPolicyFacade taskPolicyFacade) {
        this.taskRepository = taskRepository;
        this.publisher = publisher;
        this.taskMappedService = taskMappedService;
        this.toDoService = toDoService;
        this.taskPolicyFacade = taskPolicyFacade;
    }

    public TaskDto mapToResponse(Task task){
        return this.taskMappedService.toResponse(task);
    }

    public Task findById(Integer id){
        return this.taskRepository.findById(id).orElse(new Task());
    }

    @Transactional
    public Task create(CreateTaskDto taskDto, Integer userId){

        ToDoList toDoList = this.toDoService.getToDoById(
                taskDto.toDoListId(),
                userId
        );


        Task newTask = this.taskRepository.save(
                this.taskMappedService.toObject(
                        taskDto,
                        toDoList
                ));

        this.toDoService.changeTaskCount(
                toDoList.getId(),
                toDoList.getNumTask()+1
        );

        return newTask;
    }

    public Page<Task> findByToDoId(Integer toDoId, Pageable pageable, TaskFilterDto taskFilterDto){
        return this.taskRepository.findALlByToDoList_id(toDoId, pageable);
    }

    public Task updateById(Integer taskId, UpdateTaskDto updateTaskDto){
        Task task = this.taskRepository.findById(taskId).orElseThrow(()->
                new NotFoundException("Cannot found the task with id: " + taskId)
                );
        taskMappedService.patch(task, updateTaskDto);
        return this.taskRepository.save(task);
    }

    @Transactional
    public Task markDone(Integer taskId , Integer userId){
        Task task = this.taskRepository.findById(taskId).orElseThrow(()->
                new NotFoundException("Cannot found the task with id: " + taskId)
        );

        this.taskPolicyFacade.validateCanComplete(task);

        Integer toDoListId = task.getToDoList().getId();

        ToDoList toDoList = this.toDoService.getToDoById(
                toDoListId,
                userId
        );

        task.setDone(true);

        this.toDoService.changeTaskCompleteCount(
                toDoListId,
                toDoList.getNumCompleteTask() +1);

        return task;

    }

    @Transactional
    public void deleteById(
            Integer taskId,
            Integer userId
    ){
        Task task = this.taskRepository.findById(taskId).orElseThrow(()->
                new NotFoundException("Cannot found the task with id: " + taskId)
        );

        Integer toDoListId = task.getToDoList().getId();

        ToDoList toDoList = this.toDoService.getToDoById(
                toDoListId,
                userId
        );

        taskRepository.deleteById(taskId);

        this.toDoService.changeTaskCount(
                toDoList.getId(),
                toDoList.getNumTask()-1
        );
    }

}
