package dev.gnad.SpringModulith.module.Note.controller;

import dev.gnad.SpringModulith.common.api.response.ApiResponse;
import dev.gnad.SpringModulith.module.Note.domain.dto.NoteCreateDto;
import dev.gnad.SpringModulith.module.Note.domain.dto.NoteResponseDto;
import dev.gnad.SpringModulith.module.Note.domain.dto.UpdateNoteDto;
import dev.gnad.SpringModulith.module.Note.service.NoteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/note")
@Tag(
        name = "Note Management",
        description = "APIs for managing notes"
)
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<ApiResponse<NoteResponseDto>> getByTaskId(
            @PathVariable("taskId") Integer taskId
    ){
        NoteResponseDto response = this.noteService.findByTaskId(taskId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Note retrieved successfully"
                )
        );
    }

    @PostMapping("/")
    public  ResponseEntity<ApiResponse<NoteResponseDto>> createNote(
            @RequestBody() @Valid NoteCreateDto createDto
    ){
        NoteResponseDto response = this.noteService.create(createDto);

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Note created successfully"
                )
        );
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<ApiResponse<NoteResponseDto>> updateByTaskId(
            @PathVariable("taskId") Integer taskId,
            @RequestBody() @Valid UpdateNoteDto updateNoteDto
    ){
        NoteResponseDto response = this.noteService.updateByTaskId(taskId,updateNoteDto);

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Note updated successfully"
                )
        );
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<ApiResponse<Void>> deleteByTaskId(
            @PathVariable("taskId") Integer taskId
    ){
       this.noteService.deleteByTaskId(taskId);

        return ResponseEntity.ok(
                ApiResponse.success(
                       null,
                        "Note deleted successfully"
                )
        );
    }

}
