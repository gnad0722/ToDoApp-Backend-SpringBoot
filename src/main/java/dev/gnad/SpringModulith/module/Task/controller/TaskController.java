package dev.gnad.SpringModulith.module.Task.controller;

import dev.gnad.SpringModulith.common.api.response.ApiResponse;
import dev.gnad.SpringModulith.common.api.response.PageResponse;
import dev.gnad.SpringModulith.common.provider.CurrentUserProvider;
import dev.gnad.SpringModulith.module.Task.domain.dto.CreateTaskDto;
import dev.gnad.SpringModulith.module.Task.domain.dto.TaskFilterDto;
import dev.gnad.SpringModulith.module.Task.domain.dto.UpdateTaskDto;
import dev.gnad.SpringModulith.module.Task.domain.dto.TaskDto;
import dev.gnad.SpringModulith.module.Task.domain.entity.Task;
import dev.gnad.SpringModulith.module.Task.service.TaskService;
import dev.gnad.SpringModulith.module.ToDoList.service.ToDoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/task")
public class TaskController {
    private final TaskService taskService;


    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/{id}")
    public ResponseEntity <ApiResponse<TaskDto>> findById(
            @PathVariable("id") Integer id)
    {

             TaskDto response= this.taskService.mapToResponse(this.taskService.findById(id));
             return ResponseEntity.ok(
                     ApiResponse.success(
                             response,
                             "Task retrieved successfully")
             );
    }

    @PostMapping()
    public ResponseEntity <ApiResponse<TaskDto>> create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody()CreateTaskDto taskDto)
    {
        TaskDto response = this.taskService.mapToResponse(
                this.taskService.create(
                        taskDto,
                        CurrentUserProvider.getUserId(jwt)
                ));
        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Task created successfully")
        );
    }

    @GetMapping("/todo/{id}")
    public ResponseEntity<ApiResponse<PageResponse<TaskDto>>> findByToDoId(
            @ModelAttribute TaskFilterDto taskFilterDto,
            @PathVariable("id") Integer toDoId,
            Pageable pageable)
    {
            Page<Task> tasks = this.taskService.findByToDoId(toDoId, pageable, taskFilterDto);
            PageResponse<TaskDto> response = PageResponse.from(tasks, this.taskService::mapToResponse);
            return ResponseEntity.ok(
                    ApiResponse.success(
                            response,
                            "Tasks retrieved successfully "
                    )
            );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskDto>> updateByTaskId(
            @PathVariable("id") Integer id,
            @RequestBody UpdateTaskDto updateTaskDto){
        TaskDto response = this.taskService.mapToResponse(
                this.taskService.updateById(id, updateTaskDto));

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Update Task with Id: " + id + "is Successfully"
                )
        );
    }

    @PatchMapping("/done/{id}")
    public ResponseEntity<ApiResponse<TaskDto>> markDone(
            @PathVariable("id") Integer id,
            @AuthenticationPrincipal Jwt jwt){
        TaskDto response = this.taskService.mapToResponse(
                this.taskService.markDone(
                        id,
                        CurrentUserProvider.getUserId(jwt)
                ));

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Mark Done Task with Id: " + id + " successfully"
                )
        );
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(
            @PathVariable Integer id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        this.taskService.deleteById(
                id,
                CurrentUserProvider.getUserId(jwt)
        );

        return ResponseEntity.ok(
                ApiResponse.success(null, "Task deleted successfully")
        );
    }
}

