package dev.gnad.SpringModulith.module.Project.controller;

import dev.gnad.SpringModulith.common.api.response.ApiResponse;
import dev.gnad.SpringModulith.common.api.response.PageResponse;
import dev.gnad.SpringModulith.common.provider.CurrentUserProvider;
import dev.gnad.SpringModulith.common.utility.filter.FilterCondition;
import dev.gnad.SpringModulith.common.utility.filter.FilterSpecification;
import dev.gnad.SpringModulith.module.Project.domain.dto.CreateProjectDto;
import dev.gnad.SpringModulith.module.Project.domain.dto.ProjectResponseDto;
import dev.gnad.SpringModulith.module.Project.domain.dto.UpdateProjectDto;
import dev.gnad.SpringModulith.module.Project.domain.entity.Project;
import dev.gnad.SpringModulith.module.Project.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;
import java.util.List;


@Controller
@RequestMapping("/api/project")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<PageResponse<ProjectResponseDto>>> getMyProject(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) List<String> filter,
            Pageable pageable
    ){
        List<FilterCondition> conditions = FilterSpecification.parseFilters(filter);

        Page<Project> projects = this.projectService.findByUserId(
                CurrentUserProvider.getUserId(jwt),
                pageable,
                conditions
        );

        PageResponse<ProjectResponseDto> response = PageResponse.from(
                projects,
                this.projectService::toResponse
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Project retrieved successfully"
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponseDto>> getProjectById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") Integer id
    ){
        ProjectResponseDto response = this.projectService.toResponse(
                this.projectService.findById(id, CurrentUserProvider.getUserId(jwt))
        );
        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Project retrieved successfully"
                )
        );
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<ProjectResponseDto>> creatProject(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody() @Valid CreateProjectDto createProjectDto
            ){
        ProjectResponseDto response = this.projectService.toResponse(
                this.projectService.create(createProjectDto, CurrentUserProvider.getUserId(jwt))
        );
        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Project created successfully"
                )
        );
    }


    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponseDto>> updateProject(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") Integer id,
            @RequestBody UpdateProjectDto updateProjectDto
            ){
        ProjectResponseDto response = this.projectService.toResponse(
                this.projectService.updateById(
                        updateProjectDto,
                        id,
                        CurrentUserProvider.getUserId(jwt))
        );
        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Project updated successfully"
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") Integer id
    ){
        this.projectService.deleteById(id, CurrentUserProvider.getUserId(jwt));
        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        "Project deleted successfully"
                )
        );
    }
}
