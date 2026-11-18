package dev.gnad.SpringModulith.module.Note.service;

import dev.gnad.SpringModulith.common.api.exception.ConflictException;
import dev.gnad.SpringModulith.common.api.exception.NotFoundException;
import dev.gnad.SpringModulith.common.cache.constants.CacheName;
import dev.gnad.SpringModulith.module.Note.domain.dto.NoteCreateDto;
import dev.gnad.SpringModulith.module.Note.domain.dto.NoteResponseDto;
import dev.gnad.SpringModulith.module.Note.domain.dto.UpdateNoteDto;
import dev.gnad.SpringModulith.module.Note.domain.entity.Note;
import dev.gnad.SpringModulith.module.Note.internal.repository.NoteRepository;
import dev.gnad.SpringModulith.module.Task.domain.entity.Task;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
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

    @Transactional
    public NoteResponseDto create(NoteCreateDto createDto){
        if (this.noteRepository.existsByTask_id(createDto.taskId())){
            throw new ConflictException("This task already has a note");
        }
        Note note = this.noteRepository.save(
                this.noteMappedService.toObject(createDto)
        );
        return this.noteMappedService.toResponse(note);
    }


    @Cacheable(key = "#taskId")
    public NoteResponseDto findByTaskId(Integer taskId){
        Note note = this.noteRepository.findByTask_id(taskId)
                .orElseThrow(()->
                        new NotFoundException("Note not found")
                        );
        return this.noteMappedService.toResponse(note);
    }

    @CacheEvict(key = "#taskId")
    @Transactional
    public NoteResponseDto updateByTaskId(Integer taskId, UpdateNoteDto updateNoteDto){
        Note note = this.noteRepository.findByTask_id(taskId)
                .orElseThrow(()->
                        new NotFoundException("Note not found")
                );

        note.setDescription(updateNoteDto.description());
        return this.noteMappedService.toResponse(
                this.noteRepository.save(note)
        );
    }

    @CacheEvict(key = "#taskId")
    @Transactional
    public void deleteByTaskId(Integer taskId){
        Note note = this.noteRepository.findByTask_id(taskId)
                .orElseThrow(()->
                        new NotFoundException("Note not found")
                );
        this.noteRepository.deleteByTask_id(taskId);
    }




}
