package dev.gnad.SpringModulith.common.database;

import dev.gnad.SpringModulith.module.ToDoList.domain.entity.ToDoList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BaseRepository<T> extends JpaRepository<T,Integer>,
        JpaSpecificationExecutor<T> {
}
