package dev.gnad.SpringModulith.module.ToDoList.internal.repository;

import dev.gnad.SpringModulith.common.database.BaseRepository;
import dev.gnad.SpringModulith.module.ToDoList.domain.entity.ToDoList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ToDoRepository extends BaseRepository<ToDoList> {
    Optional<ToDoList> findByIdAndUser_Id(Integer id, Integer userId);
}
