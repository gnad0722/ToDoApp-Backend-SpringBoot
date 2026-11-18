package dev.gnad.SpringModulith.module.Project.service;

import dev.gnad.SpringModulith.common.policy.PolicyFacade;
import dev.gnad.SpringModulith.module.Auth.domain.entity.User;
import dev.gnad.SpringModulith.module.Auth.service.AuthService;
import dev.gnad.SpringModulith.module.Project.domain.dto.CreateProjectDto;
import dev.gnad.SpringModulith.module.Project.domain.dto.ProjectResponseDto;
import dev.gnad.SpringModulith.module.Project.domain.dto.UpdateProjectDto;
import dev.gnad.SpringModulith.module.Project.domain.entity.Project;
import org.springframework.stereotype.Service;


@Service
public class ProjectMappedService {

    private final AuthService authService;

    private final PolicyFacade policyFacade;

    public ProjectMappedService(AuthService authService,
                                PolicyFacade policyFacade
                               ) {
        this.authService = authService;
        this.policyFacade = policyFacade;
    }


    public ProjectResponseDto toResponse(Project project){
        return new ProjectResponseDto(
                project.getId(),
                project.getTopic(),
                project.getDescription(),
                project.getStartTime(),
                project.getExpectEnd(),
                project.getNumTask(),
                project.getNumCompleteTask(),
                project.getCreatedAt(),
                project.getLastModifiedAt()
        );
    }


    public Project toObject(CreateProjectDto createProjectDto, Integer userId){
        Project newProject = new Project();

        newProject.setTopic(createProjectDto.topic());
        newProject.setDescription(createProjectDto.description());


        if (createProjectDto.startTime() != null){
            this.policyFacade.validateDueDate(createProjectDto.startTime());
            newProject.setStartTime(createProjectDto.startTime());

        }

        if (createProjectDto.expectEnd() != null){
            this.policyFacade.validateTimeRange(
                    newProject.getStartTime(),
                    createProjectDto.expectEnd());
            newProject.setExpectEnd(createProjectDto.expectEnd());
        }



        User user = this.authService.getReference(userId);

        newProject.setUser(user);

        return newProject;
    }

    public void patch(Project project, UpdateProjectDto updateProjectDto){

        if (updateProjectDto.topic() != null){
            project.setTopic(updateProjectDto.topic());
        }

        if (updateProjectDto.description() != null){
            project.setDescription(updateProjectDto.description());
        }

        if (updateProjectDto.startTime() != null){
            this.policyFacade.validateDueDate(updateProjectDto.startTime());
            project.setStartTime(updateProjectDto.startTime());
        }

        if (updateProjectDto.expectEnd() != null){
            this.policyFacade.validateTimeRange(
                    project.getStartTime(),
                    updateProjectDto.expectEnd());
            project.setExpectEnd(updateProjectDto.expectEnd());
        }

    }
}
