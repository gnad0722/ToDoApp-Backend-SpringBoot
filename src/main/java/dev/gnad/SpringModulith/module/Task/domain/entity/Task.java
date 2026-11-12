package dev.gnad.SpringModulith.module.Task.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import dev.gnad.SpringModulith.common.domain.BaseEntity;
import dev.gnad.SpringModulith.module.Note.domain.entity.Note;
import dev.gnad.SpringModulith.module.ToDoList.domain.entity.ToDoList;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
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


    //The default value of this field is start time of to do list which it belongs to
    private LocalDateTime expectStart;


    private LocalDateTime expectEnd;

    @Column(nullable = false)
    private boolean isDone = false;

    @OneToOne(
            mappedBy = "task",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true)
    @JsonManagedReference
    private Note note;


}
