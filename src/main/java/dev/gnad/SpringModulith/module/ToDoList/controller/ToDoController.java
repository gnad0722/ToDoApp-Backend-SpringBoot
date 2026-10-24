package dev.gnad.SpringModulith.module.ToDoList.controller;

import dev.gnad.SpringModulith.common.api.response.ApiResponse;
import dev.gnad.SpringModulith.common.api.response.PageResponse;
import dev.gnad.SpringModulith.common.provider.CurrentUserProvider;
import dev.gnad.SpringModulith.common.utility.filter.FilterCondition;
import dev.gnad.SpringModulith.common.utility.filter.FilterSpecification;
import dev.gnad.SpringModulith.module.ToDoList.domain.dto.CreateToDoDto;
import dev.gnad.SpringModulith.module.ToDoList.domain.dto.ToDoResponseDto;
import dev.gnad.SpringModulith.module.ToDoList.domain.dto.UpdateToDoDto;
import dev.gnad.SpringModulith.module.ToDoList.domain.entity.ToDoList;
import dev.gnad.SpringModulith.module.ToDoList.service.ToDoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController()
@RequestMapping("/api/todo")
@Tag(
        name = "To Do List Management",
        description = "APIs for managing to-do list"
)
public class ToDoController {

    private final ToDoService toDoService;

    public ToDoController(ToDoService toDoService) {
        this.toDoService = toDoService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<PageResponse<ToDoResponseDto>>> getMyToDo(
          @AuthenticationPrincipal Jwt jwt,
          @RequestParam(required = false) List<String> filters,
          Pageable pageable)
    {
        List<FilterCondition> conditions = FilterSpecification.parseFilters(filters);

        Page<ToDoList> toDoLists = this.toDoService.findByUserId(
                CurrentUserProvider.getUserId(jwt)
                , pageable,
                conditions
        );
        PageResponse<ToDoResponseDto> response = PageResponse.from(
                toDoLists,
                this.toDoService::mapToResponse
        );
        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "To Do List retrieved successfully"
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ToDoResponseDto>> findById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") Integer id){
            ToDoResponseDto responseDto = this.toDoService.mapToResponse(
                    this.toDoService.getToDoById(id, CurrentUserProvider.getUserId(jwt)));
            return ResponseEntity.ok(
                    ApiResponse.success(
                            responseDto,
                            "To Do List retrieved successfully.")
            );
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<ApiResponse<PageResponse<ToDoResponseDto>>> findByUserId(
            @PathVariable("id") Integer userId,
            @RequestParam(required = false) List<String> filters,
            Pageable pageable)
    {

        List<FilterCondition> conditions = FilterSpecification.parseFilters(filters);

        Page<ToDoList> toDoLists = this.toDoService.findByUserId(userId, pageable, conditions);
        PageResponse<ToDoResponseDto> response = PageResponse.from(
                toDoLists,
                this.toDoService::mapToResponse
        );
        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "To Do List retrieved successfully"
                )
        );
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<ToDoResponseDto>> create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid  @RequestBody CreateToDoDto toDo){
        ToDoResponseDto responseDto =this.toDoService.mapToResponse(
                toDoService.createToDo(toDo, CurrentUserProvider.getUserId(jwt))
        );
        return ResponseEntity.ok(
                ApiResponse.success(
                        responseDto,
                        "To Do List created successfully.")
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<ToDoResponseDto>> updateToDoById(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody UpdateToDoDto toDoDto,
            @PathVariable("id") Integer id)
    {
        ToDoResponseDto response = this.toDoService.mapToResponse(
                this.toDoService.updateToDoById(
                        toDoDto,
                        id,
                        CurrentUserProvider.getUserId(jwt)
                ));
        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "To Do List updated successfully"
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteToDoById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") Integer id
    ){
            this.toDoService.deleteToDoById(id, CurrentUserProvider.getUserId(jwt));
            return ResponseEntity.ok(
                    ApiResponse.success(
                            null,
                            "To Do List deleted successfully"
                    )
            );
    }
}
