package dev.gnad.SpringModulith.module.Note.internal.repository;

import dev.gnad.SpringModulith.common.database.BaseRepository;
import dev.gnad.SpringModulith.module.Note.domain.entity.Note;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NoteRepository extends BaseRepository<Note> {
    Optional<Note> findByTask_id(Integer taskId);
    void deleteByTask_id(Integer taskId);
}
