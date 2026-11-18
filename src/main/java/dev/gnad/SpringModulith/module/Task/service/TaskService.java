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

    /**
     * Returns the managed entity (not cached). Use this from other modules
     * when an entity is needed to build a relation, e.g. Note -> Task.
     */
    public Task getReference(Integer id){
        return this.taskRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Task not found")
        );
    }

    @Cacheable(key = "#id")
    public TaskDto findById(Integer id){
        return this.taskMappedService.toResponse(this.getReference(id));
    }

    @Transactional
    public TaskDto create(CreateTaskDto taskDto, Integer userId){

        ToDoList toDoList = this.toDoService.getReference(taskDto.toDoListId());

        Task newTask = this.taskRepository.save(
                this.taskMappedService.toObject(
                        taskDto,
                        toDoList
                ));

        this.toDoService.changeTaskCount(
                toDoList.getId(),
                toDoList.getNumTask()+1
        );

        return this.taskMappedService.toResponse(newTask);
    }

    public Page<TaskDto> findByToDoId(Integer toDoId,
                                      Pageable pageable,
                                      List<FilterCondition> conditionList){
        Specification<Task> specification = FilterSpecification.build(
                conditionList,
                TaskFilterField.TASK_FILTER_FIELDS
        );

        return this.taskRepository.findAll( specification.and(
                FilterSpecification.belongsTo(toDoId,"toDoList")
        )
                ,pageable)
                .map(this.taskMappedService::toResponse);
    }

    @CacheEvict(key = "#taskId")
    public TaskDto updateById(Integer taskId, UpdateTaskDto updateTaskDto){
        Task task = this.taskRepository.findById(taskId).orElseThrow(()->
                new NotFoundException("Task not found")
                );
        taskMappedService.patch(task, updateTaskDto);
        return this.taskMappedService.toResponse(
                this.taskRepository.save(task)
        );
    }

    @Transactional
    @CacheEvict(key = "#taskId")
    public TaskDto markDone(Integer taskId , Integer userId){
        Task task = this.taskRepository.findById(taskId).orElseThrow(()->
                new NotFoundException("Task not found")
        );

        this.taskPolicyFacade.validateCanComplete(task);

        ToDoList toDoList = task.getToDoList();


        task.setDone(true);

        this.toDoService.changeTaskCompleteCount(
                toDoList.getId(),
                toDoList.getNumCompleteTask() +1);

        return this.taskMappedService.toResponse(task);

    }

    @Transactional
    @CacheEvict(key = "#taskId")
    public void deleteById(
            Integer taskId,
            Integer userId
    ){
        Task task = this.taskRepository.findById(taskId).orElseThrow(()->
                new NotFoundException("Task not found")
        );

        ToDoList toDoList = task.getToDoList();

        taskRepository.deleteById(taskId);

        this.toDoService.changeTaskCount(
                toDoList.getId(),
                toDoList.getNumTask()-1
        );
    }

}
