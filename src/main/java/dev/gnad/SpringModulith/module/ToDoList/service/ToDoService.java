package dev.gnad.SpringModulith.module.ToDoList.service;

import dev.gnad.SpringModulith.common.api.exception.NotFoundException;
import dev.gnad.SpringModulith.common.cache.constants.CacheName;
import dev.gnad.SpringModulith.common.schedule.service.ScheduleService;
import dev.gnad.SpringModulith.common.utility.filter.FilterCondition;
import dev.gnad.SpringModulith.common.utility.filter.FilterSpecification;
import dev.gnad.SpringModulith.module.ToDoList.domain.dto.CreateToDoDto;
import dev.gnad.SpringModulith.module.ToDoList.domain.dto.ToDoFilter;
import dev.gnad.SpringModulith.module.ToDoList.domain.dto.ToDoResponseDto;
import dev.gnad.SpringModulith.module.ToDoList.domain.dto.UpdateToDoDto;
import dev.gnad.SpringModulith.module.ToDoList.domain.entity.ToDoList;
import dev.gnad.SpringModulith.module.ToDoList.internal.repository.ToDoRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * A Project is a subclass of ToDoList (JOINED inheritance), so a project id is
 * also a valid to-do id. Write operations here evict both the toDoList cache and
 * the project cache so neither keeps a stale DTO.
 */
@Service
@CacheConfig(cacheNames = CacheName.ToDoList)
public class ToDoService {

    private final ToDoRepository toDoRepository;
    private final ApplicationEventPublisher publisher;
    private final ToDoMappedService toDoMappedService;
    private final ScheduleService scheduleService;

    public ToDoService(ToDoRepository toDoRepository,
                       ApplicationEventPublisher publisher,
                       ToDoMappedService toDoMappedService,
                       ScheduleService scheduleService){
        this.publisher = publisher;
        this.toDoRepository = toDoRepository;
        this.toDoMappedService = toDoMappedService;
        this.scheduleService = scheduleService;
    }

    /**
     * Returns the managed entity (not cached). Use this from other modules
     * when an entity is needed to build a relation, e.g. Task -> ToDoList.
     */
    public ToDoList getReference(Integer id) {
        return toDoRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("To-do list not found")
        );
    }

    @Cacheable(key = "#toDoId")
    public ToDoResponseDto getToDoById(Integer toDoId, Integer userId){

        return this.toDoMappedService.toResponse(this.toDoRepository.findByIdAndUser_Id(
                toDoId,
                userId
        ).orElseThrow(
                () -> new NotFoundException("To-do list not found")
                )
        );
    }


    public ToDoResponseDto createToDo(CreateToDoDto createToDoDto,
                                      Integer userId){

        ToDoList toDoList = this.toDoRepository.save(
               this.toDoMappedService.toObject(createToDoDto,userId));
        this.scheduleService.createSchedule(toDoList);
        return this.toDoMappedService.toResponse(toDoList);
    }

    @CacheEvict(key = "#toDoId")
    public ToDoResponseDto updateToDoById(
            UpdateToDoDto updateToDoDto,
            Integer toDoId,
            Integer userId
            ){
        ToDoList toDoList = this.toDoRepository.findByIdAndUser_Id(
                toDoId,
                userId
        ).orElseThrow(()->
                    new NotFoundException("To-do list not found")
                );

        this.toDoMappedService.patch(toDoList, updateToDoDto);
        if (updateToDoDto.startTime() != null ){
            this.scheduleService.updateRemindTime(toDoList);
        }
        return this.toDoMappedService.toResponse(
                this.toDoRepository.save(toDoList)
        );
    }


    @CacheEvict(key = "#toDoId")
    public void deleteToDoById(
            Integer toDoId,
            Integer userId){
        ToDoList toDoList = this.toDoRepository.findByIdAndUser_Id(
                toDoId,
                userId
        ).orElseThrow(()->
                new NotFoundException("To-do list not found")
        );
        this.scheduleService.cancelSchedule(toDoList);
        this.toDoRepository.deleteById(toDoList.getId());
    }



    public Page<ToDoResponseDto> findByUserId(
            Integer userId,
            Pageable pageable,
            List<FilterCondition> conditionList){

        Specification<ToDoList> specification = FilterSpecification.build(
                conditionList,
                ToDoFilter.To_Do_FILTER_FIELDS
        );


        return this.toDoRepository.findAll(
                specification.and(
                        FilterSpecification.belongsTo(userId,"user")
        )
                , pageable)
                .map(this.toDoMappedService::toResponse);
    }


    @CacheEvict( key = "#toDoId")
    public void changeTaskCount(Integer toDoId, Integer taskCount){
        ToDoList toDoList = this.getReference(toDoId);
        toDoList.setNumTask(taskCount);
        this.toDoRepository.save(toDoList);
    }

    @CacheEvict( key = "#toDoId")
    public void changeTaskCompleteCount(Integer toDoId, Integer taskCount){
        ToDoList toDoList = this.getReference(toDoId);
        toDoList.setNumCompleteTask(taskCount);
        this.toDoRepository.save(toDoList);
    }
}
