package dev.gnad.SpringModulith.module.Note.service;

import dev.gnad.SpringModulith.common.api.exception.NotFoundException;
import dev.gnad.SpringModulith.common.cache.constants.CacheName;
import dev.gnad.SpringModulith.module.Note.domain.dto.NoteCreateDto;
import dev.gnad.SpringModulith.module.Note.domain.dto.NoteResponseDto;
import dev.gnad.SpringModulith.module.Note.domain.dto.UpdateNoteDto;
import dev.gnad.SpringModulith.module.Note.domain.entity.Note;
import dev.gnad.SpringModulith.module.Note.internal.repository.NoteRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = CacheName.Note)
public class NoteService {

    private final NoteRepository noteRepository;

    private final NoteMappedService noteMappedService;

    private final AuthenticationEventPublisher eventPublisher;

    public NoteService(NoteRepository noteRepository, NoteMappedService noteMappedService,
                       AuthenticationEventPublisher eventPublisher) {
        this.noteRepository = noteRepository;
        this.noteMappedService = noteMappedService;
        this.eventPublisher = eventPublisher;
    }

    public NoteResponseDto toResponse(Note note){
        return this.noteMappedService.toResponse(note);
    }

    public Note create(NoteCreateDto createDto){
        return this.noteRepository.save(
                this.noteMappedService.toObject(createDto)
        );
    }


    @Cacheable(key = "#taskId")
    public Note findByTaskId(Integer taskId){
        return this.noteRepository.findByTask_id(taskId)
                .orElseThrow(()->
                        new NotFoundException("Not found note for task with Id: "+ taskId)
                        );
    }

    @CachePut(key = "#taskId")
    public Note updateByTaskId(Integer taskId, UpdateNoteDto updateNoteDto){
        Note note = this.noteRepository.findByTask_id(taskId)
                .orElseThrow(()->
                        new NotFoundException("Not found note for task with Id: "+ taskId)
                );

        note.setDescription(updateNoteDto.description());
        return this.noteRepository.save(note);
    }

    @CacheEvict(key = "#taskId")
    public void deleteByTaskId(Integer taskId){
        Note note = this.noteRepository.findByTask_id(taskId)
                .orElseThrow(()->
                        new NotFoundException("Not found note for task with Id: "+ taskId)
                );
        this.noteRepository.deleteByTask_id(taskId);
    }




}
