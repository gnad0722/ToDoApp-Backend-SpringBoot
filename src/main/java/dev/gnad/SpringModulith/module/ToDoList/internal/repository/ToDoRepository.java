package dev.gnad.SpringModulith.module.ToDoList.internal.repository;

import dev.gnad.SpringModulith.module.ToDoList.domain.entity.ToDoList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ToDoRepository extends JpaRepository<ToDoList, Integer> {

    Page<ToDoList> findByUser_id(Integer userId, Pageable pageable);
    Optional<ToDoList> findByIdAndUserId(Integer id, Integer userId);
}
