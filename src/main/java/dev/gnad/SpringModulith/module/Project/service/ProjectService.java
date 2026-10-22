package dev.gnad.SpringModulith.module.Project.service;

import dev.gnad.SpringModulith.common.api.exception.NotFoundException;
import dev.gnad.SpringModulith.module.Project.domain.dto.CreateProjectDto;
import dev.gnad.SpringModulith.module.Project.domain.dto.ProjectResponseDto;
import dev.gnad.SpringModulith.module.Project.domain.dto.UpdateProjectDto;
import dev.gnad.SpringModulith.module.Project.domain.entity.Project;
import dev.gnad.SpringModulith.module.Project.internal.repository.ProjectRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
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

    public ProjectResponseDto toResponse(Project project){
        return this.projectMappedService.toResponse(project);
    }

    public Project create(CreateProjectDto createProjectDto,
                          Integer userId){
        return this.projectRepository.save(
                this.projectMappedService.toObject(createProjectDto, userId)
        );
    }

    public Project findById(Integer id ,
                            Integer userId){
        return this.projectRepository.findByIdAndUserId(id, userId).
                orElseThrow(()->
                        new NotFoundException("Not Found Project with id: "+ id +"owned by user Id: "+ userId)
                );
    }

    public Page<Project> findByUserId(Integer userId,
                                      Pageable pageable){
        return this.projectRepository.findByUser_id(userId, pageable);
    }

    public Project updateById(UpdateProjectDto updateProjectDto,
                              Integer id,
                              Integer userId
    ){
        Project project = this.projectRepository.findByIdAndUserId(id, userId).
                orElseThrow(()->
                        new NotFoundException("Not Found Project with id: "+ id +"owned by user Id: "+ userId)
                );

        this.projectMappedService.patch(project,updateProjectDto);

        return this.projectRepository.save(project);
    }

    public void deleteById(Integer id,
                           Integer userId){
        Project project = this.projectRepository.findByIdAndUserId(id, userId).
                orElseThrow(()->
                        new NotFoundException("Not Found Project with id: "+ id +"owned by user Id: "+ userId)
                );

        this.projectRepository.deleteById(project.getId());

    }

}
