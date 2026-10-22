package dev.gnad.SpringModulith.module.Project.internal.repository;

import dev.gnad.SpringModulith.common.database.BaseRepository;
import dev.gnad.SpringModulith.module.Project.domain.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends BaseRepository<Project> {
    Optional<Project> findByIdAndUserId(Integer id, Integer userId);
    Page<Project> findByUser_id(Integer userId, Pageable pageable);
}
