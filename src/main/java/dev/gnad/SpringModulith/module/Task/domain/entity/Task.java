package dev.gnad.SpringModulith.module.Task.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import dev.gnad.SpringModulith.common.domain.BaseEntity;
import dev.gnad.SpringModulith.module.ToDoList.domain.entity.ToDoList;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Data
public class Task extends BaseEntity {

    private String name;

    private String description;

    @ManyToOne()
    @JoinColumn(name = "todo_id")
    @JsonBackReference
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ToDoList toDoList;


    private LocalDateTime expectStart;


    private LocalDateTime expectEnd;

    @Column(nullable = false)
    private boolean isDone = false;


}
