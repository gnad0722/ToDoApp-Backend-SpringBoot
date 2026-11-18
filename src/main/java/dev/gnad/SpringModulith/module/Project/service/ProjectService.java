package dev.gnad.SpringModulith.module.Project.service;

import dev.gnad.SpringModulith.common.api.exception.NotFoundException;
import dev.gnad.SpringModulith.common.cache.constants.CacheName;
import dev.gnad.SpringModulith.common.utility.filter.FilterCondition;
import dev.gnad.SpringModulith.common.utility.filter.FilterSpecification;
import dev.gnad.SpringModulith.module.Project.domain.dto.CreateProjectDto;
import dev.gnad.SpringModulith.module.Project.domain.dto.ProjectFilter;
import dev.gnad.SpringModulith.module.Project.domain.dto.ProjectResponseDto;
import dev.gnad.SpringModulith.module.Project.domain.dto.UpdateProjectDto;
import dev.gnad.SpringModulith.module.Project.domain.entity.Project;
import dev.gnad.SpringModulith.module.Project.internal.repository.ProjectRepository;
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
 * Projects have their own cache: it holds ProjectResponseDto, while the toDoList
 * cache holds ToDoResponseDto. Sharing one cache would make the deserializer
 * return the wrong type. Writes also evict the toDoList entry because a project
 * can be read through the to-do endpoints too.
 */
@Service
@CacheConfig(cacheNames = CacheName.Project)
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ProjectMappedService projectMappedService;

    public ProjectService(ProjectRepository projectRepository,
                          ApplicationEventPublisher applicationEventPublisher,
                          ProjectMappedService projectMappedService) {
        this.projectRepository = projectRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.projectMappedService = projectMappedService;
    }

    public ProjectResponseDto create(CreateProjectDto createProjectDto,
                                     Integer userId){
        return this.projectMappedService.toResponse(
                this.projectRepository.save(
                        this.projectMappedService.toObject(createProjectDto, userId)
                )
        );
    }

    @Cacheable(key = "#id")
    public ProjectResponseDto findById(Integer id ,
                                       Integer userId){
        return this.projectMappedService.toResponse(
                this.projectRepository.findByIdAndUser_Id(id, userId).
                        orElseThrow(()->
                                new NotFoundException("Project not found")
                        )
        );
    }

    public Page<ProjectResponseDto> findByUserId(Integer userId,
                                                 Pageable pageable,
                                                 List<FilterCondition> conditions){
        Specification<Project> specification = FilterSpecification.build(
                conditions,
                ProjectFilter.PROJECT_FILTER_FIELDS
        );
        return this.projectRepository.findAll(
                specification.and(
                        FilterSpecification.belongsTo(userId,"user")
                )
                , pageable)
                .map(this.projectMappedService::toResponse);
    }

    @CacheEvict( key = "#id")
    public ProjectResponseDto updateById(UpdateProjectDto updateProjectDto,
                                         Integer id,
                                         Integer userId
    ){
        Project project = this.projectRepository.findByIdAndUser_Id(id, userId).
                orElseThrow(()->
                        new NotFoundException("Project not found")
                );

        this.projectMappedService.patch(project,updateProjectDto);

        return this.projectMappedService.toResponse(
                this.projectRepository.save(project)
        );
    }


    @CacheEvict( key = "#id")
    public void deleteById(Integer id,
                           Integer userId){
        Project project = this.projectRepository.findByIdAndUser_Id(id, userId).
                orElseThrow(()->
                        new NotFoundException("Project not found")
                );

        this.projectRepository.deleteById(project.getId());

    }

}
