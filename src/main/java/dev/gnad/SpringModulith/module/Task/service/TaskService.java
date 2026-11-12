package dev.gnad.SpringModulith.module.Task.service;

import dev.gnad.SpringModulith.common.api.exception.NotFoundException;
import dev.gnad.SpringModulith.common.cache.constants.CacheName;
import dev.gnad.SpringModulith.common.utility.filter.FilterCondition;
import dev.gnad.SpringModulith.common.utility.filter.FilterSpecification;
import dev.gnad.SpringModulith.module.Task.domain.dto.CreateTaskDto;
import dev.gnad.SpringModulith.module.Task.domain.dto.TaskFilterField;
import dev.gnad.SpringModulith.module.Task.domain.dto.UpdateTaskDto;
import dev.gnad.SpringModulith.module.Task.domain.dto.TaskDto;
import dev.gnad.SpringModulith.module.Task.domain.entity.Task;
import dev.gnad.SpringModulith.module.Task.internal.repository.TaskRepository;
import dev.gnad.SpringModulith.module.Task.policy.TaskPolicyFacade;
import dev.gnad.SpringModulith.module.ToDoList.domain.entity.ToDoList;
import dev.gnad.SpringModulith.module.ToDoList.service.ToDoService;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = CacheName.Task)
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

    @Cacheable(key = "#id")
    public Task findById(Integer id){
        return this.taskRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Not found task with id: "+id)
        );
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

    public Page<Task> findByToDoId(Integer toDoId,
                                   Pageable pageable,
                                   List<FilterCondition> conditionList){
        Specification<Task> specification = FilterSpecification.build(
                conditionList,
                TaskFilterField.TASK_FILTER_FIELDS
        );

        return this.taskRepository.findAll( specification.and(
                FilterSpecification.belongsTo(toDoId,"toDoList")
        )
                ,pageable);
    }

    @CachePut(key = "#taskId")
    public Task updateById(Integer taskId, UpdateTaskDto updateTaskDto){
        Task task = this.taskRepository.findById(taskId).orElseThrow(()->
                new NotFoundException("Cannot found the task with id: " + taskId)
                );
        taskMappedService.patch(task, updateTaskDto);
        return this.taskRepository.save(task);
    }

    @Transactional
    @CachePut(key = "#taskId")
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
    @CacheEvict(key = "#taskId")
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
