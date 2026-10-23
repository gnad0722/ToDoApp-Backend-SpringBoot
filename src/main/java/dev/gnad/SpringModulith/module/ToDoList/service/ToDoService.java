package dev.gnad.SpringModulith.module.ToDoList.service;

import dev.gnad.SpringModulith.common.api.exception.NotFoundException;
import dev.gnad.SpringModulith.common.utility.filter.FilterCondition;
import dev.gnad.SpringModulith.common.utility.filter.FilterSpecification;
import dev.gnad.SpringModulith.module.ToDoList.domain.dto.CreateToDoDto;
import dev.gnad.SpringModulith.module.ToDoList.domain.dto.ToDoFilter;
import dev.gnad.SpringModulith.module.ToDoList.domain.dto.ToDoResponseDto;
import dev.gnad.SpringModulith.module.ToDoList.domain.dto.UpdateToDoDto;
import dev.gnad.SpringModulith.module.ToDoList.domain.entity.ToDoList;
import dev.gnad.SpringModulith.module.ToDoList.internal.repository.ToDoRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ToDoService {

    private final ToDoRepository toDoRepository;
    private final ApplicationEventPublisher publisher;
    private final ToDoMappedService toDoMappedService;

    public ToDoService(ToDoRepository toDoRepository,
                       ApplicationEventPublisher publisher,
                       ToDoMappedService toDoMappedService){
        this.publisher = publisher;
        this.toDoRepository = toDoRepository;
        this.toDoMappedService = toDoMappedService;
    }

    public ToDoResponseDto mapToResponse(ToDoList toDoList) {
        return this.toDoMappedService.toResponse(toDoList);
    }

    public ToDoList getToDoById(Integer id, Integer userId){
        return this.toDoRepository.findByIdAndUserId(
                id,
                userId
        ).orElseThrow(
                () -> new NotFoundException("Cannot find To Do List with id: "+id+" and user Id: "+ userId)
                );
    }

    public ToDoList createToDo(CreateToDoDto createToDoDto, Integer userId){
       return this.toDoRepository.save(
               this.toDoMappedService.toObject(createToDoDto,userId));
    }

    public ToDoList updateToDoById(
            UpdateToDoDto updateToDoDto,
            Integer toDoId,
            Integer userId
            ){
        ToDoList toDoList = this.toDoRepository.findByIdAndUserId(
                toDoId,
                userId
        ).orElseThrow(()->
                    new NotFoundException("Not Found To Do with Id: "+ toDoId)
                );

        this.toDoMappedService.patch(toDoList, updateToDoDto);

        return this.toDoRepository.save(toDoList);
    }

    public void deleteToDoById(
            Integer toDoId,
            Integer userId){
        ToDoList toDoList = this.toDoRepository.findByIdAndUserId(
                toDoId,
                userId
        ).orElseThrow(()->
                new NotFoundException("Not Found To Do with Id: "+ toDoId)
        );
        this.toDoRepository.deleteById(toDoList.getId());
    }

    public Page<ToDoList> findByUserId(
            Integer userId,
            Pageable pageable,
            List<FilterCondition> conditionList){

        Specification<ToDoList> specification = FilterSpecification.build(
                conditionList,
                ToDoFilter.To_Do_FILTER_FIELDS
        );

        return this.toDoRepository.findByUser_id(userId,pageable, specification);
    }

    public void changeTaskCount(Integer toDoId, Integer taskCount){
        ToDoList toDoList = this.toDoRepository.findById(toDoId).
                orElseThrow(()->
                        new NotFoundException("Cannot found To Do with Id: " + toDoId)
                        );
        toDoList.setNumTask(taskCount);
        this.toDoRepository.save(toDoList);

    }

    public void changeTaskCompleteCount(Integer toDoId, Integer taskCount){
        ToDoList toDoList = this.toDoRepository.findById(toDoId).
                orElseThrow(()->
                        new NotFoundException("Cannot found To Do with Id: " + toDoId)
                );
        toDoList.setNumCompleteTask(taskCount);
        this.toDoRepository.save(toDoList);
    }
}
