package dev.gnad.SpringModulith.common.database;

import dev.gnad.SpringModulith.module.ToDoList.domain.entity.ToDoList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;


import java.time.LocalDateTime;
import java.util.Optional;
@NoRepositoryBean
public interface BaseRepository<T> extends JpaRepository<T,Integer>,
        JpaSpecificationExecutor<T> {
}
