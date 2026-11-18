package dev.gnad.SpringModulith.module.Note.service;

import dev.gnad.SpringModulith.module.Note.domain.dto.NoteCreateDto;
import dev.gnad.SpringModulith.module.Note.domain.dto.NoteResponseDto;
import dev.gnad.SpringModulith.module.Note.domain.entity.Note;
import dev.gnad.SpringModulith.module.Task.domain.entity.Task;
import dev.gnad.SpringModulith.module.Task.service.TaskService;
import org.springframework.stereotype.Service;

@Service
public class NoteMappedService {

    private final TaskService taskService;

    public NoteMappedService(TaskService taskService) {
        this.taskService = taskService;
    }

    public Note toObject(NoteCreateDto createDto){
        Note note = new Note();
        note.setDescription(createDto.description());
        Task task = this.taskService.getReference(createDto.taskId());
        note.setTask(task);
        return note;
    }

    public NoteResponseDto toResponse(Note note){
        return new NoteResponseDto(
                note.getId(),
                note.getDescription(),
                note.getCreatedAt(),
                note.getLastModifiedAt(),
                note.getTask().getId()
        );
    }
}
