package dev.gnad.SpringModulith.module.Task.internal.repository;

import dev.gnad.SpringModulith.module.Task.domain.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;




@Repository
public interface TaskRepository  extends JpaRepository<Task,Integer> {

    Page<Task> findALlByToDoList_id(Integer toDoId, Pageable pageable);
}
